package group.cc.occ.controller;

import group.cc.core.Result;
import group.cc.core.ResultCode;
import group.cc.core.ResultGenerator;
import group.cc.occ.model.User;
import group.cc.occ.model.dto.LoginUserDto;
import group.cc.occ.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import group.cc.occ.util.CusAccessObjectUtil;
import group.cc.occ.util.RedisUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyuming
 * @date 2019/03/01
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/occ/user")
public class UserController {
    @Resource
    private UserService userService;

    @Autowired
    private HttpServletRequest request; //自动注入request

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private Log log = LogFactory.getLog(UserController.class);

    @ApiOperation("添加 User")
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        userService.save(user);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("删除 User")
    @DeleteMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        userService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("更新 User")
    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.update(user);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("通过 Id 查询 User 详情")
    @GetMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        User user = userService.findById(id);
        return ResultGenerator.genSuccessResult(user);
    }

    @ApiOperation("分页查询 User 列表")
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<User> list = userService.findAll();
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation(value="登录")
    @GetMapping(value="/login")
    public Result login(@RequestParam()String account, @RequestParam()String password) {
        LoginUserDto loginUserDto = null;
        try {
            loginUserDto = userService.login(account, password);
            RedisUtil.login(redisTemplate, request, loginUserDto);
            loginUserDto = RedisUtil.getLoginInfo(redisTemplate, request);
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genFailResult(e.getMessage());
        }

        return ResultGenerator.genSuccessResult(loginUserDto);
    }

    @ApiOperation(value="注册")
    @PostMapping(value="/register")
    public Result register(@RequestBody User user) {
        LoginUserDto loginUserDto = null;
       ;
        try {
            loginUserDto = userService.register(user);
            RedisUtil.login(redisTemplate, request, loginUserDto);
        }catch (Exception e){
            e.printStackTrace();
            return ResultGenerator.genFailResult(e.getMessage());
        }
        return ResultGenerator.genSuccessResult(loginUserDto);
    }

    @ApiOperation(value="注销登录")
    @GetMapping(value="/singUp")
    public Result singUp() {
        RedisUtil.singUp(redisTemplate, request);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value="获取用户信息")
    @GetMapping(value="/getUser")
    public Result getUser() {
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);
        if(login == null)
            return new Result().setCode(ResultCode.UNAUTHORIZED).setMessage("用户未登录！");
        return ResultGenerator.genSuccessResult(login);
    }

    @ApiOperation(value="返回未登录提示")
    @GetMapping(value="/unLogin")
    public Result unLogin() {
        return new Result().setCode(ResultCode.UNAUTHORIZED).setMessage("用户尚未登录！");
    }

    @ApiOperation("分页查询 User 列表带模糊查询")
    @GetMapping("/listByKey")
    public Result listByKey(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size,
                            @RequestParam(defaultValue = "") String key, @RequestParam(defaultValue = "") String value) {
        PageHelper.startPage(page, size);
        List<User> list = null;
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);

        if("".equals(key))
            list = userService.findAllByLoginOrg(login);
        else
            list = userService.listByKey(key, value, login);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @ApiOperation("分页查询 User 列表带模糊查询")
    @GetMapping("/findUserByIdOrNameByLoginOrg")
    public Result findUserByIdOrNameByLoginOrg(@RequestParam(defaultValue = "") String value) {
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);
        List<User> list = userService.findUserByIdOrNameNotLoginOrg(value, login.getOrganization().getId());
        return ResultGenerator.genSuccessResult(list);
    }

    @ApiOperation("切换部门")
    @GetMapping("/switchOrg")
    public Result switchOrg(@RequestParam() Integer orgId) {
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);
        try {
            login = userService.switchOrg(orgId, login);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult(e.getMessage()).setCode(ResultCode.FAIL).setData(new Date());
        }
        RedisUtil.login(redisTemplate, request, login);
        return ResultGenerator.genSuccessResult(login);
    }

    @ApiOperation("查找该机构的用户")
    @GetMapping("/getUserByLoginOrgId")
    public Result getUserByLoginOrgId() {
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);
        List<User> list = userService.getUserByLoginOrgId(login);
        return ResultGenerator.genSuccessResult(list);
    }

    @ApiOperation("查找该机构的用户")
    @GetMapping("/removeUser")
    public Result removeUser(@RequestParam()Integer userId) {
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);
        this.userService.removeUser(userId, login.getOrganization().getId());
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("查找该机构的用户")
    @GetMapping("/removeAllUser")
    public Result removeUser(@RequestParam()List<Integer> userIds) {
        LoginUserDto login = RedisUtil.getLoginInfo(redisTemplate, request);
        userIds.forEach(userId ->{
            this.userService.removeUser(userId, login.getOrganization().getId());
        });

        return ResultGenerator.genSuccessResult();
    }
}
