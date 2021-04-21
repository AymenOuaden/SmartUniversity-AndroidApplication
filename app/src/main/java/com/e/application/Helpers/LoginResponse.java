package com.e.application.Helpers;

public class LoginResponse {

        private String token = null;
        private int id = 0;

        public LoginResponse(String token, int id)
        {
            this.token = token;
            this.id = id;
        }

        public LoginResponse()
        {

        }

        public String getToken()
        {
            return token;
        }

        public int getId()
        {
            return id;
        }

        public void setToken(String token)
        {
            this.token = token;
        }

        public void setId(int id)
        {
            this.id = id;
        }
}
