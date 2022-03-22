package com.dl.playfun.manager;

import com.dl.playfun.app.Injection;
import com.dl.playfun.data.AppRepository;

/**
 * 权限管理
 *
 * @author wulei
 */
public class PermissionManager {
    public static PermissionManager instance;
    private final AppRepository repository;

    private PermissionManager() {
        repository = Injection.provideDemoRepository();
    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    /**
     * 是否允许查看用户主页
     *
     * @param checkUserSex 性别 1男 0女
     * @return
     */
    public boolean canCheckUserDetail(Integer checkUserSex) {
        if (checkUserSex == null) {
            return false;
        }
        if (checkUserSex == 0) {
            return canCheckFemaleDetail();
        } else if (checkUserSex == 1) {
            return canCheckMaleDetail();
        } else {
            return false;
        }
    }

    /**
     * 是否允许收藏用户
     *
     * @param checkUserSex 性别 1男 0女
     * @return
     */
    public boolean canCollectUser(Integer checkUserSex) {
        if (checkUserSex == null) {
            return false;
        }
        if (checkUserSex == 0) {
            return canCheckFemaleDetail();
        } else if (checkUserSex == 1) {
            return canCheckMaleDetail();
        } else {
            return false;
        }
    }

    /**
     * 是否可以查看男性主页
     *
     * @return
     */
    public boolean canCheckMaleDetail() {
        if (repository.readUserData() == null || repository.readUserData().getSex() == null) {
            return false;
        }
        return repository.readUserData().getSex() != null && repository.readUserData().getSex() == 0;
    }

    /**
     * 是否可以查看女性个人主页
     *
     * @return
     */
    public boolean canCheckFemaleDetail() {
        if (repository.readUserData() == null || repository.readUserData().getSex() == null) {
            return false;
        }
        return repository.readUserData().getSex() != null && repository.readUserData().getSex() == 1;
    }

    /**
     * 是否可以在电台评论
     *
     * @return
     */
    public boolean canRadioComment() {
        if (isMale()) {
            return repository.readUserData().getIsVip() == 1;
        } else if (isFemale()) {
            return repository.readIsVerifyFace();
        }
        return false;
    }

    /**
     * 是否可以报名节目
     *
     * @return
     */
    public boolean canSignUpProgram() {
        return repository.readIsVerifyFace();
    }

    public boolean isMale() {
        return repository.readUserData().getSex() != null && repository.readUserData().getSex() == 1;
    }

    public boolean isFemale() {
        return repository.readUserData().getSex() != null && repository.readUserData().getSex() == 0;
    }
}
