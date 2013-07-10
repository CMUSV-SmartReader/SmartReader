$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = "http://localhost:9000/import-feeds";
    console.log("now bind fileupload");
    $('#fileupload').fileupload({
        url: url,
        sequentialUploads: true
    });
});