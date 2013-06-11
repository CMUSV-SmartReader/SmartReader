var smartreader = smartreader || {};

smartreader.ui = (function(){

  var
    binding = function(){
      $('#menu-toggle-btn').on('click', function(){
        $('#side-container').toggleClass('span3').toggleClass('span1');
        $('#content-container').toggleClass('span9').toggleClass('span11');
      });

      $('#side-menu').on('click', 'li', function(){
        var feedid = $(this).attr("data-feedid");
        console.log(feedid);
        if(feedid){
          $.ajax({
            dataType: "json",
            url: "/feed/"+feedid,
            success: function(data){
              console.log(data);
              var articles = [];
              for(var i=0; i<data.articles.length; ++i){
                articles.push( new smartreader.article(
                  data.articles[i].title,
                  data.articles[i].author || data.title,
                  data.articles[i].publishDate,
                  data.title,
                  data.articles[i].desc
                ))
              }
              smartreader.core.putTitle(data.title);
              smartreader.core.putArticles(articles);
            }
          });
        }
      });
    };

  return {
    init : function(){
      $('#side-container').perfectScrollbar();
      $('#content-container').perfectScrollbar();
      binding();
    }
  };

})();
