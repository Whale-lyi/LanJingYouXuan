package top.whalefall.controller;


import org.springframework.web.bind.annotation.*;
import top.whalefall.dto.Result;
import top.whalefall.service.IFollowService;

import javax.annotation.Resource;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private IFollowService followService;

    @PutMapping("/{id}/{ifFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("ifFollow") Boolean ifFollow) {
        return followService.follow(followUserId, ifFollow);
    }

    @GetMapping("/or/not/{id}")
    public Result followOrNot(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable("id") Long followUserId) {
        return followService.followCommons(followUserId);
    }

}
