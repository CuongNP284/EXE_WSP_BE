package com.wsp.workshophy.service;

import com.wsp.workshophy.constant.PostStatus;
import com.wsp.workshophy.dto.request.PostCreationRequest;
import com.wsp.workshophy.dto.response.ParagraphResponse;
import com.wsp.workshophy.dto.response.PostResponse;
import com.wsp.workshophy.entity.Paragraph;
import com.wsp.workshophy.entity.Post;
import com.wsp.workshophy.entity.User;
import com.wsp.workshophy.exception.AppException;
import com.wsp.workshophy.exception.ErrorCode;
import com.wsp.workshophy.mapper.PostMapper;
import com.wsp.workshophy.repository.PostRepository;
import com.wsp.workshophy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostRepository postRepository;
    UserRepository userRepository;
    PostMapper postMapper;

    public PostResponse createPost(PostCreationRequest request) {
        User author = getCurrentUser();
        Post post = postMapper.toPost(request);
        post.setAuthor(author);
        post.setDescription(request.getDescription());
        post.setCreatedDate(post.getCreatedDate());
        post.setImage(request.getImage());

        // Admin đăng bài thì tự động APPROVED, còn lại là PENDING
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        post.setStatus(isAdmin ? PostStatus.APPROVED : PostStatus.PENDING);

        // Tạo các paragraph
        List<Paragraph> paragraphs = request.getParagraphs().stream()
                .map(p -> Paragraph.builder()
                        .content(p.getContent())
                        .image(p.getImage())
                        .post(post)
                        .build())
                .collect(Collectors.toList());
        post.setParagraphs(paragraphs);

        return mapToPostResponse(postRepository.save(post));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PostResponse> getAllPostsForAdmin() {
        return postRepository.findAllByActive(true).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PostResponse getPostForAdmin(Long postId) {
        Post post = findPostByIdAndActive(postId, true);
        return mapToPostResponse(post);
    }

    public List<PostResponse> getAllApprovedPosts() {
        return postRepository.findAllByActiveAndStatus(true, PostStatus.APPROVED)
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    public PostResponse getPostDetail(Long postId) {
        Post post = findPostByIdAndActive(postId, true);
        if (!post.getStatus().equals(PostStatus.APPROVED)) {
            throw new AppException(ErrorCode.POST_NOT_AVAILABLE);
        }
        return mapToPostResponse(post);
    }

    public List<PostResponse> getMyPostHistory() {
        User author = getCurrentUser();
        return postRepository.findAllByAuthorAndActive(author, true) // Thay đổi sang findAllByAuthorAndActive
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deletePost(Long postId) {
        Post post = findPostByIdAndActive(postId, true);
        post.setActive(false); // Deactivate post
        post.getParagraphs().forEach(p -> p.setActive(false)); // Deactivate paragraphs
        postRepository.save(post);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PostResponse approvePost(Long postId) {
        Post post = findPostByIdAndActive(postId, true);
        post.setStatus(PostStatus.APPROVED);
        return mapToPostResponse(postRepository.save(post));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PostResponse rejectPost(Long postId) {
        Post post = findPostByIdAndActive(postId, true);
        post.setStatus(PostStatus.REJECTED);
        return mapToPostResponse(postRepository.save(post));
    }

    private Post findPostByIdAndActive(Long postId, boolean active) {
        return postRepository.findByIdAndActive(postId, active)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    private PostResponse mapToPostResponse(Post post) {
        PostResponse response = postMapper.toPostResponse(post);
        response.setDescription(post.getDescription());
        List<ParagraphResponse> paragraphResponses = post.getParagraphs().stream()
                .map(p -> {
                    ParagraphResponse pr = new ParagraphResponse();
                    pr.setId(p.getId());
                    pr.setContent(p.getContent());
                    pr.setImage(p.getImage());
                    return pr;
                })
                .collect(Collectors.toList());
        response.setParagraphs(paragraphResponses);
        return response;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndActive(email, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
