package com.five.tools;

/**'
 * 前端发送一个登入请求，然后这个来接收JSON的用户和密码
 */
public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}