package com.software.grey.recommendationsystem;

import com.software.grey.models.dtos.PostFilterDTO;
import com.software.grey.models.entities.Post;
import com.software.grey.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class Recommender {
    private Combiner combiner;
    private SameFeelingStrat sameFeelingStrat;
    private InverseFeelingStrat inverseFeelingStrat;
    private SecurityUtils securityUtils;

    public List<Post> recommend(PostFilterDTO postFilterDTO) {
        StrategyPercentage stratPercent = buildStrategies();

        // request posts here and pass it to combiner
        List<Post> SameFeelingPosts = getSameFeelingRecommendation(postFilterDTO, stratPercent);
        List<Post> inverseFeelingPosts = getInverseFeelingRecommendation(postFilterDTO, stratPercent);

        List<List<Post>> aggreagtedPosts = new ArrayList<>();
        aggreagtedPosts.add(SameFeelingPosts);
        aggreagtedPosts.add(inverseFeelingPosts);

        // sort by date, and remove duplicates
        return combiner.combine(aggreagtedPosts);
    }

    private StrategyPercentage buildStrategies() {
        StrategyPercentage strategyPercentage = new StrategyPercentage();
        strategyPercentage.addNewStrategyPercentage(sameFeelingStrat, 80);
        strategyPercentage.addNewStrategyPercentage(inverseFeelingStrat, 20);
        return strategyPercentage;
    }

    private List<Post> getSameFeelingRecommendation(PostFilterDTO postFilterDTO, StrategyPercentage stratPercent) {
        return sameFeelingStrat.recommend(
                securityUtils.getCurrentUser(),
                postFilterDTO.getPageNumber(),
                calculateNumberOfPostsFromPercentage(
                        postFilterDTO.getPageSize(),
                        stratPercent.getStrategyPercentage(sameFeelingStrat)
                )
        );
    }

    private List<Post> getInverseFeelingRecommendation(PostFilterDTO postFilterDTO, StrategyPercentage stratPercent) {
        return inverseFeelingStrat.recommend(
                securityUtils.getCurrentUser(),
                postFilterDTO.getPageNumber(),
                calculateNumberOfPostsFromPercentage(
                        postFilterDTO.getPageSize(),
                        stratPercent.getStrategyPercentage(inverseFeelingStrat)
                )
        );
    }

    private int calculateNumberOfPostsFromPercentage(int totalSize, double percentage) {
        return (int)(percentage * totalSize);
    }
}