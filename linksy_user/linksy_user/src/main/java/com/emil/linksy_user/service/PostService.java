package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.repository.UserPostCommentRepository;
import com.emil.linksy_user.repository.UserPostLikeRepository;
import com.emil.linksy_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserPostCommentRepository userPostCommentRepository;
    private final UserPostLikeRepository userPostLikeRepository;
    private final LinksyCacheManager linksyCacheManager;


    @KafkaListener(topics = "postResponse", groupId = "group_id_post", containerFactory = "postKafkaResponseKafkaListenerContainerFactory")
    public void consumePost(PostKafkaResponse response) {
        Long authorId = response.getAuthorId();
        User author = linksyCacheManager.getUserById(authorId);
        if (response.getPostId()==null) {
            Post newPost = new Post();
            newPost.setUser(author);
            newPost.setText(response.getText());
            newPost.setImageUrl(response.getImageUrl());
            newPost.setVideoUrl(response.getVideoUrl());
            newPost.setAudioUrl(response.getAudioUrl());
            newPost.setVoiceUrl(response.getVoiceUrl());
            newPost.setEdited(false);
            newPost.setReposts(0L);
            postRepository.save(newPost);
        }else{
            Post editPost = postRepository.findById(response.getPostId())
                    .orElseThrow(() -> new NotFoundException("Post not found"));
            if (!editPost.getUser().getId().equals(authorId))  throw new SecurityException("User does not own the post");
            editPost.setText(response.getText());
            editPost.setImageUrl(response.getImageUrl());
            editPost.setVideoUrl(response.getVideoUrl());
            editPost.setAudioUrl(response.getAudioUrl());
            editPost.setVoiceUrl(response.getVoiceUrl());
            editPost.setEdited(true);
            postRepository.save(editPost);
        }
    }


    public List<PostResponse> getUserPosts(Long userId) {
        User user = linksyCacheManager.getUserById(userId);

        List<Post> posts = postRepository.findByUser(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        return posts.stream()
                .sorted((post1, post2) -> post2.getPublicationTime().compareTo(post1.getPublicationTime()))
                .map(post ->{
                        Long likesCount = userPostLikeRepository.countByPost(post);
                        Long commentsCount = userPostCommentRepository.countByPost(post);
                        Boolean isLikedIt = userPostLikeRepository.existsByPostAndUser(post,user);
                        Boolean edited = post.getEdited();
                       return new PostResponse(
                        post.getId(),
                        post.getUser().getId(),
                        user.getUsername(),
                        user.getAvatarUrl(),
                        post.getImageUrl(),
                        post.getVideoUrl(),
                        post.getAudioUrl(),
                        post.getVoiceUrl(),
                        post.getText(),
                        dateFormat.format(post.getPublicationTime()),
                               likesCount,
                        commentsCount,
                        post.getReposts(), isLikedIt,edited

                );}).toList();
    }


    public List<PostResponse> toPostResponse (User finder,List<Post> posts){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        return posts.stream()
                .sorted((post1, post2) -> post2.getPublicationTime().compareTo(post1.getPublicationTime()))
                .map(post ->{
                    Long likesCount = userPostLikeRepository.countByPost(post);
                    Long commentsCount = userPostCommentRepository.countByPost(post);
                    Boolean isLikedIt = userPostLikeRepository.existsByPostAndUser(post,finder);
                    Boolean edited = post.getEdited();
                    return new PostResponse(
                            post.getId(),
                            post.getUser().getId(),
                            post.getUser().getUsername(),
                            post.getUser().getAvatarUrl(),
                            post.getImageUrl(),
                            post.getVideoUrl(),
                            post.getAudioUrl(),
                            post.getVoiceUrl(),
                            post.getText(),
                            dateFormat.format(post.getPublicationTime()),
                            likesCount,
                            commentsCount,
                            post.getReposts(), isLikedIt,edited

                    );}).toList();
    }



    public List<PostResponse> getOutsiderUserPosts(Long finderId,Long userId) {
        User user = linksyCacheManager.getUserById(userId);
        User finder = linksyCacheManager.getUserById(finderId);

        List<Post> posts = postRepository.findByUser(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        return posts.stream()
                .sorted((post1, post2) -> post2.getPublicationTime().compareTo(post1.getPublicationTime()))
                .map(post ->{
                    Long likesCount = userPostLikeRepository.countByPost(post);
                    Long commentsCount = userPostCommentRepository.countByPost(post);
                    Boolean isLikedIt = userPostLikeRepository.existsByPostAndUser(post,finder);
                    Boolean edited = post.getEdited();
                    return new PostResponse(
                            post.getId(),
                            post.getUser().getId(),
                            user.getUsername(),
                            user.getAvatarUrl(),
                            post.getImageUrl(),
                            post.getVideoUrl(),
                            post.getAudioUrl(),
                            post.getVoiceUrl(),
                            post.getText(),
                            dateFormat.format(post.getPublicationTime()),
                            likesCount,
                            commentsCount,
                            post.getReposts(), isLikedIt,edited

                    );}).toList();
    }

    @Transactional
public void deletePost (Long userId,long postId) {
    User user = linksyCacheManager.getUserById(userId);
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    if (!post.getUser().getId().equals(user.getId()))
        throw new SecurityException("User does not own the post");

    var likes = userPostLikeRepository.findByPost(post);
    likes.forEach(userPostLikeRepository::delete);
    userPostCommentRepository.deleteByPostAndParentIdIsNotNull(post);
    userPostCommentRepository.deleteByPostAndParentIdIsNull(post);
    postRepository.delete(post);
}


  public void addLike (Long userId,Long postId){
      User user = linksyCacheManager.getUserById(userId);
      Post post = postRepository.findById(postId)
              .orElseThrow(() -> new IllegalArgumentException("Post not found"));
      UserPostLike like = new UserPostLike();
      like.setUser(user);
      like.setPost(post);
      userPostLikeRepository.save(like);
  }


  public void deleteLike (Long userId,Long postId){
      User user = linksyCacheManager.getUserById(userId);
      Post post = postRepository.findById(postId)
              .orElseThrow(() -> new IllegalArgumentException("Post not found"));
      UserPostLike like = userPostLikeRepository.findByPostAndUser(post,user);
      userPostLikeRepository.delete(like);
  }

  public void addComment (Long userId,CommentRequest comment){
      User user = linksyCacheManager.getUserById(userId);
      Post post = postRepository.findById(comment.getPostId())
              .orElseThrow(() -> new IllegalArgumentException("Post not found"));
      UserPostComment newComment = new UserPostComment();
      newComment.setUser(user);
      newComment.setPost(post);
      newComment.setText(comment.getText());
      if (comment.getParentCommentId()!=null){
          UserPostComment parentComment = userPostCommentRepository.findById(comment.getParentCommentId())
                  .orElseThrow(() -> new NotFoundException("ParentComment not found"));
          newComment.setParent(parentComment);
      }
      userPostCommentRepository.save(newComment);
  }


    public List<CommentResponse> getAllPostComments (Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        var comments = userPostCommentRepository.findByPost(post);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
      return comments.stream()
                 .map(comment -> {
                     User author = comment.getUser();
                     Long parentId = comment.getParent() == null ? null : comment.getParent().getId();
                     return new CommentResponse(comment.getId(), author.getId(), author.getAvatarUrl(), author.getUsername(), parentId,comment.getText(),
                     dateFormat.format(comment.getDate()));
                 }).toList();
    }

    @Transactional
    public void deleteComment (Long userId,Long commentId){
        UserPostComment comment = userPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getUser().getId().equals(userId)) throw new AccessDeniedException("THE USER DID NOT LEAVE A COMMENT");
        var childComments = userPostCommentRepository.findByParent(comment);
        for (UserPostComment comm :childComments){
            comm.setParent(null);
            userPostCommentRepository.save(comm);
        }
        userPostCommentRepository.delete(comment);
    }

}
