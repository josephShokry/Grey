package com.software.grey.recommendationsystem;

import com.software.grey.models.entities.Post;
import com.software.grey.models.entities.User;
import com.software.grey.models.enums.Feeling;
import com.software.grey.repositories.PostRepository;
import com.software.grey.repositories.UserRepo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InverseFeelingStratIntegrationTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    InverseFeelingStrat inverseFeelingStrat;
    private User user1;
    private User user2;
    @BeforeAll
    void init() {
        user1 = User.builder().email("InverseFeeling@example.com").username("InverseFeeling").build();
        user2 = User.builder().email("InverseFeeling2@example.com").username("InverseFeeling2").build();
        userRepo.save(user1);
        userRepo.save(user2);
        for(int i = 0; i < 30; i++) {
            Set<Feeling> set = new TreeSet<>();
            set.add(Feeling.HAPPY);
            Post post = Post.builder().postText("Sad").user(user2).postFeelings(set).build();
            postRepository.save(post);
        }
        for(int i = 0; i < 30; i++) {
            Set<Feeling> set = new TreeSet<>();
            set.add(Feeling.ANXIOUS);
            Post post = Post.builder().postText("Anxious").user(user2).postFeelings(set).build();
            postRepository.save(post);
        }
    }

    @AfterAll
    void del(){
        postRepository.deleteAll();
    }

    @Test
    void WhenUserIsSad_ShouldReturnHappy() {
        Set<Feeling> set = new TreeSet<>();
        set.add(Feeling.SAD);
        Post post = Post.builder().postText("Ta3ban").user(user1).postFeelings(set).build();
        postRepository.save(post);

        List<Post> posts = inverseFeelingStrat.recommend(user1, 0, 10);
        assertEquals(10, posts.size());
        for(Post p: posts) {
            assertTrue(p.getPostFeelings().contains(Feeling.HAPPY));
        }
    }
}