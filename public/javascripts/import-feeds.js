$(function () {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = "http://localhost:9000/import-feeds";
    
//    $('#manage-importfeed-btn').click(function(){
//        $('#import-result').html();
//        $('#progress .bar').css();
//    });
    
    var length = $('#manage-importfeed-btn').length;
    console.log("triggered in import-feed.js " + length);
    $('#fileupload').fileupload({
        url: url,
        done: function (e, data) {
            console.log("fileupload done.");
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('#progress .bar').css(
                'width',
                progress + '%'
            );
            if (progress >= 99) {
                setTimeout(function(){
                    $("#import-result").removeClass();
                    $("#import-result").addClass("alert alert-success").html("File uploaded. We are preparing for your feeds. It can take several minutes.");
                }, 1000);
            }
        },
        add: function (e, data) {
            var goUpload = true;
            var uploadFile = data.files[0];
            if (!(/\.(xml)$/i).test(uploadFile.name)) {
                $("#import-result").removeClass();
                $("#import-result").addClass("alert alert-danger").html("Invalid file type. Please use the .xml file in your Google Takeout zip file.");
                goUpload = false;
            }
            if (uploadFile.size > 5000000) { // 5mb
                $("#import-result").removeClass();
                $("#import-result").addClass("alert alert-danger").html('Please upload a smaller image, max size is 5 MB');
                goUpload = false;
            }
            if (goUpload == true) {
                data.submit();
            }
        }
    });
});