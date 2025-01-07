package org.app;

import java.util.*;

import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import javafx.application.Application;

import java.util.concurrent.TimeUnit;
import java.io.*;

import javax.swing.*;
import java.awt.event.*;

import org.app.model.*;
import org.app.view.CustomControl;

public class Main {

    public static void main(String[] args) {
        /*String cwd = System.getProperty("user.dir");
        System.out.println("Current working directory: " + cwd);*/
        Application.launch(App.class, args);
    }

}
