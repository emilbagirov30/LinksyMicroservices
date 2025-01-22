package com.emil.linksy_user.service;

import com.emil.linksy_user.exception.NotFoundException;
import com.emil.linksy_user.model.*;
import com.emil.linksy_user.repository.*;
import com.emil.linksy_user.util.ChannelType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ChannelPostRepository channelPostRepository;
    private final PollRepository pollRepository;
    private final PollOptionsRepository pollOptionsRepository;
    private final ChannelSubscriptionsRequestRepository channelSubscriptionsRequestRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelPostCommentsRepository channelPostCommentsRepository;
    private final ChannelPostEvaluationsRepository channelPostEvaluationsRepository;
    private final UserRepository userRepository;
    private final VoterRepository voterRepository;

    @KafkaListener(topics = "channelResponse", groupId = "group_id_channel", containerFactory = "channelKafkaResponseKafkaListenerContainerFactory")
    public void consumeChannel(ChannelKafkaResponse response) {
        if (response.getChannelId() == null) {
            User owner = userRepository.findById(response.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            Channel channel = new Channel();
            channel.setOwner(owner);
            channel.setName(response.getName());
            if (channelRepository.existsByLink(response.getLink())) {
                channel.setLink(null);
            } else {
                channel.setLink(response.getLink());
            }
            channel.setAvatarUrl(response.getAvatarUrl());
            channel.setDescription(response.getDescription());
            channel.setType(response.getType());
            channelRepository.save(channel);
            ChannelMember channelMember = new ChannelMember();
            channelMember.setChannel(channel);
            channelMember.setUser(owner);
            channelMemberRepository.save(channelMember);
        } else {
            Channel channel = channelRepository.findById(response.getChannelId())
                    .orElseThrow(() -> new NotFoundException("Channel not found"));
            channel.setName(response.getName());
            if (channelRepository.existsByLinkAndNotId(response.getLink(), response.getChannelId())) {
                channel.setLink(null);
            } else {
                channel.setLink(response.getLink());
            }
            channel.setAvatarUrl(response.getAvatarUrl());
            channel.setDescription(response.getDescription());
            channel.setType(response.getType());
            channelRepository.save(channel);
        }
    }


    public List<ChannelResponse> findByLink(String prefix) {
        var channels = channelRepository.findByLinkStartingWith(prefix);
        return mapToChannelResponse(channels);
    }


    public List<ChannelResponse> findByName(String prefix) {
        var channels = channelRepository.findByNameStartingWith(prefix);
        return mapToChannelResponse(channels);
    }

    private List<ChannelResponse> mapToChannelResponse(List<Channel> channels) {
        return channels.stream().map(channel -> {
            var posts = channelPostRepository.findByChannel(channel);
            Double averageRating = posts.stream()
                    .map(post -> channelPostEvaluationsRepository.findAverageScoreByChannelPostId(post.getId()))
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            return new ChannelResponse(channel.getId(), channel.getOwner().getId(), channel.getName(),
                    channel.getLink(), channel.getAvatarUrl(), Math.round(averageRating * 100.0) / 100.0,
                    channel.getType());
        }).toList();
    }

    public List<ChannelResponse> getChannels(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<ChannelMember> channelMembers = channelMemberRepository.findByUser(user);
        var channels = channelMembers.stream().map(ChannelMember::getChannel);
        return channels.map(channel -> {
            var posts = channelPostRepository.findByChannel(channel);
            Double averageRating = posts.stream()
                    .map(post -> channelPostEvaluationsRepository.findAverageScoreByChannelPostId(post.getId()))
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            return new ChannelResponse(channel.getId(), channel.getOwner().getId(), channel.getName(),
                    channel.getLink(), channel.getAvatarUrl(), Math.round(averageRating * 100.0) / 100.0, channel.getType());

        }).toList();
    }


    public ChannelPageData getChannelPageData(Long finderId, Long channelId) {
        User finder = userRepository.findById(finderId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        List<ChannelMember> channelMembers = channelMemberRepository.findByChannel(channel);
        Long memberCount = (long) channelMembers.size();
        boolean isMember = channelMembers.stream()
                .anyMatch(channelMember -> channelMember.getUser().equals(finder));

        var type = channel.getType();
        String avatarUrl = channel.getAvatarUrl();
        String name = channel.getName();
        String description = channel.getDescription();
        String link = channel.getLink();

        var requests = channelSubscriptionsRequestRepository.findByChannel(channel);
        Boolean isSubmitted = channelSubscriptionsRequestRepository.findByUserAndChannel(finder, channel) != null;
        var posts = channelPostRepository.findByChannel(channel);

        Double averageRating = posts.stream()
                .map(post -> channelPostEvaluationsRepository.findAverageScoreByChannelPostId(post.getId()))
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        Long ownerId = channel.getOwner().getId();

        return new ChannelPageData(
                channelId,
                ownerId,
                name,
                link,
                avatarUrl,
                description,
                isMember,
                isSubmitted,
                Math.round(averageRating * 100.0) / 100.0,
                type,
                memberCount,
                (long) requests.size()
        );
    }


    public void deleteRequest(Long userId, Long channelId) {
        User candidate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        var request = channelSubscriptionsRequestRepository.findByUserAndChannel(candidate, channel);
        channelSubscriptionsRequestRepository.delete(request);
    }

    public void submitRequest(Long userId, Long channelId) {
        User candidate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        ChannelSubscriptionsRequest request = new ChannelSubscriptionsRequest();
        request.setChannel(channel);
        request.setUser(candidate);
        channelSubscriptionsRequestRepository.save(request);
    }

    public List<UserResponse> getChannelSubscriptionRequests(Long userId, Long channelId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");
        var candidates = channelSubscriptionsRequestRepository.findByChannel(channel);

        return candidates.stream().map(candidate -> {
            User user = candidate.getUser();
            return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink(), user.getOnline(), user.getConfirmed());
        }).toList();
    }

    public void acceptUserToChannel(Long ownerId, Long channelId, Long candidateId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChannelMember channelMember = new ChannelMember();
        channelMember.setUser(candidate);
        channelMember.setChannel(channel);
        var request = channelSubscriptionsRequestRepository.findByUserAndChannel(candidate, channel);
        channelSubscriptionsRequestRepository.delete(request);
        channelMemberRepository.save(channelMember);
    }

    public void rejectSubscriptionRequest(Long ownerId, Long channelId, Long candidateId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        if (!channel.getOwner().equals(owner)) throw new SecurityException("User is not the owner channel");
        User candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        var request = channelSubscriptionsRequestRepository.findByUserAndChannel(candidate, channel);
        channelSubscriptionsRequestRepository.delete(request);
    }

    @KafkaListener(topics = "channelPostResponse", groupId = "group_id_channel", containerFactory = "channelPostKafkaResponseKafkaListenerContainerFactory")
    public void consumeChannelPost(ChannelPostKafkaResponse response) {
        User owner = userRepository.findById(response.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(response.getChannelId())
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        if (response.getPostId() == null) {

            if (!channel.getOwner().getId().equals(owner.getId()))
                throw new SecurityException("User is not the owner channel");

            ChannelPost channelPost = new ChannelPost();
            channelPost.setChannel(channel);
            channelPost.setEdited(false);
            channelPost.setText(response.getText());
            channelPost.setImageUrl(response.getImageUrl());
            channelPost.setVideoUrl(response.getVideoUrl());
            channelPost.setReposts(0L);
            if (response.getOptions() != null) {
                if (!response.getOptions().isEmpty()) {
                    Poll poll = new Poll();
                    poll.setTitle(response.getPollTitle());
                    pollRepository.save(poll);
                    channelPost.setPoll(poll);
                    var options = response.getOptions();
                    for (String option : options) {
                        PollOptions pollOptions = new PollOptions();
                        pollOptions.setPoll(poll);
                        pollOptions.setOption(option);
                        pollOptions.setSelectedCount(0L);
                        pollOptionsRepository.save(pollOptions);
                    }
                }
            }
            channelPostRepository.save(channelPost);
        } else {
            ChannelPost channelPost = channelPostRepository.findById(response.getPostId())
                    .orElseThrow(() -> new NotFoundException("Post not found"));
            channelPost.setChannel(channel);
            channelPost.setText(response.getText());
            channelPost.setImageUrl(response.getImageUrl());
            channelPost.setEdited(true);
            channelPost.setVideoUrl(response.getVideoUrl());

            if (response.getOptions() != null) {
                if (!response.getOptions().isEmpty()) {
                    Poll poll = new Poll();
                    poll.setTitle(response.getPollTitle());
                    pollRepository.save(poll);
                    channelPost.setPoll(poll);
                    var options = response.getOptions();
                    for (String option : options) {
                        PollOptions pollOptions = new PollOptions();
                        pollOptions.setPoll(poll);
                        pollOptions.setOption(option);
                        pollOptions.setSelectedCount(0L);
                        pollOptionsRepository.save(pollOptions);
                    }
                }
            } else channelPost.setPoll(null);
            channelPostRepository.save(channelPost);
        }
    }

    public List<ChannelPostResponse> getChannelsPost(Long userId, Long channelId) {
        User finder = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        List<ChannelMember> channelMembers = channelMemberRepository.findByChannel(channel);
        boolean isMember = channelMembers.stream()
                .anyMatch(channelMember -> channelMember.getUser().equals(finder));
        if (!isMember && channel.getType().equals(ChannelType.PRIVATE)) {
            throw new SecurityException("User is not a member of the channel");
        }

        var posts = channelPostRepository.findByChannel(channel);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return posts.stream()
                .sorted((post1, post2) -> post2.getPublicationTime().compareTo(post1.getPublicationTime()))
                .map(post -> {
                    Double averageRating = channelPostEvaluationsRepository.findAverageScoreByChannelPostId(post.getId());
                    if (averageRating == null) {
                        averageRating = 0.0;
                    }

                    List<OptionResponse> optionResponseList = null;
                    String title = null;
                    Boolean isVoted = false;

                    if (post.getPoll() != null) {
                        Poll poll = pollRepository.findById(post.getPoll().getId())
                                .orElseThrow(() -> new NotFoundException("Poll not found"));
                        title = poll.getTitle();
                        var options = pollOptionsRepository.findByPoll(poll);
                        isVoted = isVoted(options, finder);
                        optionResponseList = options.stream()
                                .map(op -> new OptionResponse(op.getId(), op.getOption(), op.getSelectedCount()))
                                .toList();
                    }
                    Integer userScore = channelPostEvaluationsRepository.findScoreByChannelPostIdAndUserId(post.getId(),userId);
                    Long commentsCount = channelPostCommentsRepository.countByChannelPost(post);
                    return new ChannelPostResponse(
                            post.getId(),
                            channel.getName(),
                            channel.getAvatarUrl(),
                            post.getText(),
                            post.getImageUrl(),
                            post.getVideoUrl(),
                            post.getAudioUrl(),
                            dateFormat.format(post.getPublicationTime()),
                            title,
                            isVoted,
                            optionResponseList,
                            Math.round(averageRating * 100.0) / 100.0,
                            post.getReposts(),
                            post.getEdited(),
                            post.getChannel().getOwner().getId(),
                            commentsCount,
                            userScore
                    );
                })
                .toList();
    }


    private boolean isVoted(List<PollOptions> options, User user) {
        List<Voter> voters = options.stream()
                .flatMap(option -> voterRepository.findByOption(option).stream())
                .toList();


        return voters.stream().anyMatch(voter -> voter.getUser().equals(user));
    }

    public List<UserResponse> getChannelMembers(Long userId, Long channelId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Chat not found"));
        List<ChannelMember> members = channelMemberRepository.findByChannel(channel);
        boolean isMember = members.stream().anyMatch(channelMember -> channelMember.getUser().equals(requester));
        if (!isMember) throw new AccessDeniedException("User is not a member of this chat");

        return members.stream()
                .map(member -> {
                    User user = member.getUser();
                    return new UserResponse(user.getId(), user.getAvatarUrl(), user.getUsername(), user.getLink(), user.getOnline(), user.getConfirmed());
                })
                .toList();
    }

    public void deletePost(Long userId, long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChannelPost post = channelPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        if (!post.getChannel().getOwner().equals(user)) {
            throw new SecurityException("User does not own the post");
        }
        Poll poll = post.getPoll();
        post.setPoll(null);
        channelPostRepository.delete(post);
        if (poll != null) {
            var pollOptions = pollOptionsRepository.findByPoll(poll);
            pollOptions.forEach(option -> {
                var voters = voterRepository.findByOption(option);
                voterRepository.deleteAll(voters);
            });
            pollOptions.forEach(pollOptionsRepository::delete);
            pollRepository.delete(poll);
        }
        var channelPostEvaluations = channelPostEvaluationsRepository.findByChannelPost(post);
        channelPostEvaluations.forEach(channelPostEvaluationsRepository::delete);

    }


    public void subscribe(Long subscriberId, Long channelId) {
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        ChannelMember channelMember = new ChannelMember();
        channelMember.setUser(subscriber);
        channelMember.setChannel(channel);
        channelMemberRepository.save(channelMember);
    }

    public void unsubscribe(Long subscriberId, Long channelId) {
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        ChannelMember channelMember = channelMemberRepository.findByUserAndChannel(subscriber, channel)
                .orElseThrow(() -> new NotFoundException(" ChannelMember not found"));
        channelMemberRepository.delete(channelMember);
    }


    public void vote(Long userId, Long optionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        PollOptions pollOptions = pollOptionsRepository.findById(optionId)
                .orElseThrow(() -> new NotFoundException("Not found"));
        Voter voter = new Voter();
        voter.setUser(user);
        voter.setOption(pollOptions);
        voterRepository.save(voter);
        var count = pollOptions.getSelectedCount();

        pollOptions.setSelectedCount(++count);
        pollOptionsRepository.save(pollOptions);
    }


    public ChannelManagementResponse getChannelManagementData(Long userId, Long channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));

        if (!channel.getOwner().equals(user))
            throw new AccessDeniedException("User do not own the channel");
        return new ChannelManagementResponse(channel.getName(), channel.getLink(), channel.getAvatarUrl(), channel.getDescription(), channel.getType());
    }

    public void setScore(Long userId, Long postId, int score) {
        if (score < 0 || score > 5) throw new IllegalArgumentException("incorrect score");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChannelPost post = channelPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        ChannelPostEvaluations evaluation = new ChannelPostEvaluations();
        evaluation.setScore(score);
        evaluation.setUser(user);
        evaluation.setChannelPost(post);
        channelPostEvaluationsRepository.save(evaluation);
    }

    public void deleteScore(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChannelPost post = channelPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        ChannelPostEvaluations evaluation = channelPostEvaluationsRepository.findByChannelPostAndUser(post,user);
        channelPostEvaluationsRepository.delete(evaluation);
    }
    public void addComment (Long userId,CommentRequest comment){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        ChannelPost post =channelPostRepository.findById(comment.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        ChannelPostComment newComment = new  ChannelPostComment();
        newComment.setUser(user);
        newComment.setChannelPost(post);
        newComment.setText(comment.getText());
        if (comment.getParentCommentId()!=null){
            ChannelPostComment parentComment = channelPostCommentsRepository.findById(comment.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("ParentComment not found"));
            newComment.setParent(parentComment);
        }
           channelPostCommentsRepository.save(newComment);
    }


    public List<CommentResponse> getAllPostComments (Long postId){
        ChannelPost post =channelPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        var comments = channelPostCommentsRepository.findByChannelPost(post);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        return comments.stream()
                .map(comment -> {
                    User author = comment.getUser();
                    Long parentId = comment.getParent() == null ? null : comment.getParent().getId();
                    return new CommentResponse(comment.getId(), author.getId(), author.getAvatarUrl(), author.getUsername(), parentId,comment.getText(),
                            dateFormat.format(comment.getDate()));
                }).toList();
    }








    public ChannelPostResponse toChannelPostResponse(User finder,ChannelPost post) {
        Double averageRating = channelPostEvaluationsRepository.findAverageScoreByChannelPostId(post.getId());
        if (averageRating == null) {
            averageRating = 0.0;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        List<OptionResponse> optionResponseList = null;
        String title = null;
        Boolean isVoted = false;

        if (post.getPoll() != null) {
            Poll poll = pollRepository.findById(post.getPoll().getId())
                    .orElseThrow(() -> new NotFoundException("Poll not found"));
            title = poll.getTitle();
            var options = pollOptionsRepository.findByPoll(poll);
            isVoted = isVoted(options, finder);
            optionResponseList = options.stream()
                    .map(op -> new OptionResponse(op.getId(), op.getOption(), op.getSelectedCount()))
                    .toList();
        }
        Integer userScore = channelPostEvaluationsRepository.findScoreByChannelPostIdAndUserId(post.getId(), finder.getId());
        Long commentsCount = channelPostCommentsRepository.countByChannelPost(post);
        return new ChannelPostResponse(
                post.getId(),
                post.getChannel().getName(),
                post.getChannel().getAvatarUrl(),
                post.getText(),
                post.getImageUrl(),
                post.getVideoUrl(),
                post.getAudioUrl(),
                dateFormat.format(post.getPublicationTime()),
                title,
                isVoted,
                optionResponseList,
                Math.round(averageRating * 100.0) / 100.0,
                post.getReposts(),
                post.getEdited(),
                post.getChannel().getOwner().getId(),
                commentsCount,
                userScore
        );
    }

    @Transactional
    public void deleteComment (Long userId,Long commentId){
        ChannelPostComment comment = channelPostCommentsRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!comment.getUser().getId().equals(userId)) throw new AccessDeniedException("THE USER DID NOT LEAVE A COMMENT");
        var childComments = channelPostCommentsRepository.findByParent(comment);
        for ( ChannelPostComment comm :childComments){
            comm.setParent(null);
            channelPostCommentsRepository.save(comm);
        }
        channelPostCommentsRepository.delete(comment);
    }
}