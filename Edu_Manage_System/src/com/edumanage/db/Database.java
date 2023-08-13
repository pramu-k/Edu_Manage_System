package com.edumanage.db;

import com.edumanage.model.Program;
import com.edumanage.model.Student;
import com.edumanage.model.Teacher;
import com.edumanage.model.User;
import com.edumanage.util.security.PasswordManager;

import java.util.ArrayList;

public class Database {
    public static ArrayList<User> userTable = new ArrayList();
    public static ArrayList<Student> studentTable = new ArrayList();
    public static ArrayList<Teacher> teacherTable = new ArrayList();
    public static ArrayList<Program> programTable = new ArrayList();


    static {
        userTable.add(new User("Hivin","Pramuditha","hivin@gmail.com",new PasswordManager().encrypt("1234")));

    }
}
