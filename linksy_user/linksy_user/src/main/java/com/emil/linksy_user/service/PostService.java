package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.UserNotFoundException;
import com.emil.linksy_user.model.Post;
import com.emil.linksy_user.model.PostDto;
import com.emil.linksy_user.model.User;
import com.emil.linksy_user.repository.PostRepository;
import com.emil.linksy_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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




}
