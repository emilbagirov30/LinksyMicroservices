package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.Post;
import com.emil.linksy_user.model.PostDto;
import com.emil.linksy_user.model.PostResponse;
import com.emil.linksy_user.model.User;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;



    public void createPost(Long authorId,PostDto post) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Post newPost = new Post ();
        newPost.setUser(author);
        newPost.setText(post.getText());
        postRepository.save(newPost);
    }

    public List<PostResponse> getUserPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Post> posts = postRepository.findByUser(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return posts.stream()
                .sorted((post1, post2) -> post2.getPublicationTime().compareTo(post1.getPublicationTime()))
                .map(post -> new PostResponse(
                        user.getUsername(),
                        user.getAvatarUrl(),
                        post.getText(),
                        dateFormat.format(post.getPublicationTime()),
                        post.getLikes(),
                        post.getReposts()
                ))
                .collect(Collectors.toList());
    }



}
