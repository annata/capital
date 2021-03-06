package com.xiyoukeji.service;

import com.xiyoukeji.entity.Foundation;
import com.xiyoukeji.entity.Role;
import com.xiyoukeji.entity.User;
import com.xiyoukeji.tools.BaseDao;
import com.xiyoukeji.tools.MapTool;
import com.xiyoukeji.utils.ErrCodeExcetion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by dasiy on 16/12/22.
 */
@Service
public class UserService {
    @Resource
    BaseDao<User> baseDao;
    @Resource
    HttpSession session;
    @Resource
    BaseDao<Role> roleBaseDao;
    @Resource
    BaseDao<Foundation> foundationBaseDao;
    @Resource
    BaseDao<User> userBaseDao;

    @Transactional
    public Map saveorupdateUser(User user) {
        Map map = new HashMap<>();
        User user1 = null;
        if (user.getId() == null) {
            /*新建*/
            User user2 = userBaseDao.get("from User where userName = '" + user.getUserName() + "'");
            if (user2 == null) {
                user1 = user;
                baseDao.saveOrUpdate(user1);
                map.put("userId", user.getId());
                return MapTool.Mapok().put("data", map);
            } else {
                return MapTool.Map().put("code", 4);
            }

        } else {
            User user2 = baseDao.get("from User where id != " + user.getId() + " and userName = '" + user.getUserName() + "'");
            if (user2 != null) {
                return MapTool.Map().put("code", 4);
            } else {
                user1 = baseDao.get(User.class, user.getId());
                user1.setRole(user.getRole());
                user1.setName(user.getName());
                user1.setAddress(user.getAddress());
                user1.setPhone(user.getPhone());
                user1.setPosition(user.getPosition());
                user1.setUserName(user.getUserName());
                user1.setPassword(user.getPassword());
                user1.setRemark(user.getRemark());
                user1.setList_foundation(user.getList_foundation());
                if (user.getPhoto() != null) {
                    user1.setPhoto(user.getPhoto());
                }
                baseDao.saveOrUpdate(user1);
                map.put("userId", user.getId());
                return MapTool.Mapok().put("data", map);
            }
        }


    }

    @Transactional
    public Map updatePassword(String prePass, String password) {
        Map map = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (!prePass.equals(user.getPassword())) {
            map.put("code", 1);
            return map;
        } else {
            user.setPassword(password);
            userBaseDao.saveOrUpdate(user);
            return MapTool.Map().put("code", 0);
        }


    }

    @Transactional
    public List<User> getUserListByRole(int roleId) {
        List<User> list = new ArrayList<>();
        list = baseDao.find("from User where available = 1 and role.id = " + roleId);
        return list;

    }

    @Transactional
    public List<User> getUserList(int type, Integer roleId, String nameorcode, Integer available) {
        /*, Integer roleId, String nameorcode, Integer available*/
        List<User> list = new ArrayList<>();
        List<User> list1 = new ArrayList<>();
        switch (type) {

            case 0:
                /*内部角色*/
                list = baseDao.find("from User where available = 1 and role.type = 0");
                break;
            case 1:
                list = baseDao.find("from User where available = 1 and role.type = 1");
                break;
            case 2:
                list = baseDao.find("from User where available = 1 and role.type != 2 order by role.type,role.roleName");
                break;
            case 3:
                list = baseDao.find("from User where available = 1 and role.type = 0");
                break;
            case 4:
                /*后台管理系统 禁用启用用户列表*/
                String sql = "";
                String sql1 = "";
                if (roleId != null) {
                    sql += "from User WHERE position is NOT NULL AND role.id = " + roleId;
                    sql1 += "from User WHERE position is NULL AND role.id = " + roleId;
                } else {
                    sql += "from User WHERE position is NOT NULL AND role.id is NOT NULL";
                    sql1 += "from User WHERE (position is  NULL OR role.id is NULL)";
                }
                if (nameorcode != null && !nameorcode.equals(""))/*增加心魔查询*/ {
                    sql += " AND (name like '%" + nameorcode + "%' or userName like '%" + nameorcode + "%')";
                    sql1 += " AND (name like '%" + nameorcode + "%' or userName like '%" + nameorcode + "%')";
                }
                if (available != null) {
                    sql += " AND available = " + available;
                    sql1 += " AND available = " + available;
                }
                sql += " ORDER BY role.id ASC,position ASC";
                sql1 += " ORDER BY role.id DESC,position DESC";
                list = baseDao.find(sql);
                list1 = baseDao.find(sql1);
                list.addAll(list1);


//                list = baseDao.find("from User WHERE position is NOT NULL AND role.id is NOT NULL ORDER BY role.id ASC,position ASC");
//                list1 = baseDao.find("from User WHERE position is  NULL OR role.id is NULL ORDER BY role.id DESC,position DESC");
//                list.addAll(list1);
                break;
        }
        return list;

    }

    @Transactional
    public User getUser(Integer id) {
        return baseDao.get(User.class, id);
    }

    @Transactional
    public Map update_user(Integer id, int type) {
        Map map = new HashMap<>();
        User user = baseDao.get(User.class, id);
        /*禁用*/
        if (type == 0) {
            user.setAvailable(0);
        } else {
            /*启用*/
            user.setAvailable(1);
        }
        baseDao.update(user);
        map.put("userId", user.getId());
        return map;
    }

    @Transactional
    public Map login(HttpServletResponse response, User user) {
        List<User> list = new ArrayList<>();
        Map map = new HashMap<>();
//        if (user.getSession_token() == null || user.getSession_token().equals("")) {
            /*web*/
        map.put("userName", user.getUserName());
        map.put("password", user.getPassword());
        list = baseDao.find("from User where userName = :userName and password = :password and available = 1 ", map);
        if (list.size() != 0) {
            User user1 = list.get(0);
            String cookieStr = UUID.randomUUID().toString();
            user1.setSession_token(cookieStr);
            baseDao.saveOrUpdate(user1);
            session.setAttribute("user", list.get(0));
            Cookie cookie = new Cookie("cookieStr", cookieStr);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(31536000);
            response.addCookie(cookie);
            return MapTool.Mapok().put("userId", list.get(0).getId());
        } else {
            return MapTool.Map().put("code", "5");
        }
//        } else {
//            /*订阅号*/
//            User user2 = baseDao.get("from User where session_token = '" + user.getSession_token() + "'");
//            if (user2 != null)
//                return MapTool.Mapok().put("userId", user2.getId()).put("session_token", user2.getSession_token());
//            else
//                return MapTool.Map().put("code", "6");
//        }


    }

    @Transactional
    public Map login_back(User user) {
        List<User> list = new ArrayList<>();
        Map map = new HashMap<>();
        map.put("userName", user.getUserName());
        map.put("password", user.getPassword());
        list = baseDao.find("from User where userName = :userName and password = :password and available = 1 ", map);
        if (list.size() != 0) {
            /*管理员*/
            if (list.get(0).getRole().getType() == 2) {
                session.setAttribute("user", list.get(0));
                return MapTool.Mapok().put("userId", list.get(0).getId());
            } else
            /*无权限*/
                return MapTool.Map().put("code", "3");

        } else {
            /*用户名或密码错误*/
            return MapTool.Map().put("code", "1");
        }

    }


    @Transactional
    public void logout(HttpServletResponse response) {
        User user = (User) session.getAttribute("user");
        user.setSession_token(null);
        saveorupdateUser(user);
        Cookie cookie = new Cookie("cookieStr", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        session.removeAttribute("user");
    }

    @Transactional
    public Foundation getInvestByFoundationId(Integer foundationId) {
        return foundationBaseDao.get(Foundation.class, foundationId);
    }

    public User getCookie(String value) {
        return userBaseDao.get("from User where session_token = :session_token", MapTool.Map().put("session_token", value));
    }

    public boolean isLog() {
        User user1 = (User) session.getAttribute("user");
        if (user1 == null)
            throw new ErrCodeExcetion("2");
        else
            return true;


    }

    public boolean isAuthority() {
        User user1 = (User) session.getAttribute("user");
        if (user1 == null)
            throw new ErrCodeExcetion("2");
        else if (user1.getRole().getType() != 2)
            throw new ErrCodeExcetion("3");
        else
            return true;


    }

}
