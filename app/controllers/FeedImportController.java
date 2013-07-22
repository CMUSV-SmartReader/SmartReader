package controllers;

import java.io.File;

import play.mvc.Controller;
import play.mvc.Http.RawBuffer;
import play.mvc.Result;
import play.mvc.Http.RequestBody;

public class FeedImportController extends Controller{

    public static Result upload() {
        RequestBody body = request().body();
        if (body != null) {
            RawBuffer rawBuffer = body.asRaw();
            if (rawBuffer != null) {
                File uploadedFile = rawBuffer.asFile();
            }
        }
        return ok("||||||||||||||||||||||||| upload triggered");
      }
}
