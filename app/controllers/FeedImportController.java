package controllers;

import java.io.File;

import play.mvc.Controller;
import play.mvc.Result;

public class FeedImportController extends Controller{

    public static Result upload() {
        File file = request().body().asRaw().asFile();
        return ok("||||||||||||||||||||||||| upload triggered");
      }
}
