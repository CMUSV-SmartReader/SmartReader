var thermoreader = thermoreader || {};

thermoreader.ui = (function(){

  var
    binding = function(){
      $('#menu-toggle-btn').on('click', function(){
        $('#side-container').toggleClass('span3 span1 hidden');
        $('#content-container').toggleClass('span9 span10');
      });

      /*$('#side-menu').on('click', 'li', function(){
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
                  data.articles[i].desc.slice(0, 48),
                  data.articles[i].desc,
                  data.articles[i].link,
                  Math.floor(Math.random()*5+1)
                ))
              }
              smartreader.core.putTitle(data.title);
              smartreader.core.putArticles(articles);
            }
          });
        }
      });*/

      /*$('#content-container').on('click', '.continue-article', function(){
        if($(this).html() != 'Less...'){
          $(this).html('Less...');
          $(this).parentsUntil("#content").addClass('read');
        }
        else {
          $(this).html('Continue Reading...');
        }
        $(this).prev().prev('.article-summary').toggleClass('hidden');
        $(this).prev('.article-description').toggleClass('hidden');
      });*/

    };

  return {
    init : function(){
      $('#side-container').perfectScrollbar({wheelSpeed: 60});
      $('#content-container').perfectScrollbar({wheelSpeed: 60});
      binding();
    }
  };

})();
