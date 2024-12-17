package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.Post;
import com.emil.linksy_user.model.PostDto;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.model.User;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.repository.UserRepository;
import com.emil.linksy_user.util.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MediaService mediaService;


    public void createPost(Long authorId,PostDto post) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Post newPost = new Post();
        newPost.setUser(author);
        String text = post.getText();
        if (!text.trim().isEmpty()) newPost.setText(post.getText());
        postRepository.save(newPost);
        MultipartFile image = post.getImage();
        MultipartFile video = post.getVideo();
        MultipartFile audio = post.getAudio();
        MultipartFile voice = post.getVoice();
        if (image!=null) mediaService.requestPostResourcesUpload(newPost.getId(), image, Topic.IMAGE_POST_REQUEST);
        if (video!=null) mediaService.requestPostResourcesUpload(newPost.getId(), video, Topic.VIDEO_POST_REQUEST);
        if (audio!=null) mediaService.requestPostResourcesUpload(newPost.getId(), audio, Topic.AUDIO_POST_REQUEST);
        if (voice!=null) mediaService.requestPostResourcesUpload(newPost.getId(), voice, Topic.VOICE_POST_REQUEST);
    }

    public List<PostResponse> getUserPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Post> posts = postRepository.findByUser(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return posts.stream()
                .sorted((post1, post2) -> post2.getPublicationTime().compareTo(post1.getPublicationTime()))
                .map(post -> new PostResponse(
                        post.getId(),
                        user.getUsername(),
                        user.getAvatarUrl(),
                        post.getImageUrl(),
                        post.getVideoUrl(),
                        post.getAudioUrl(),
                        post.getVoiceUrl(),
                        post.getText(),
                        dateFormat.format(post.getPublicationTime()),
                        post.getLikes(),
                        post.getReposts()
                ))
                .collect(Collectors.toList());
    }

public void deletePost (Long userId,long postId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
    if (!post.getUser().getId().equals(user.getId())) {
        throw new SecurityException("User does not own the post");
    }
    postRepository.delete(post);
}

}
