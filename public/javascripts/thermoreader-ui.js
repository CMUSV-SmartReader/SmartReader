var thermoreader = thermoreader || {};

thermoreader.ui = (function(){

  var
    binding = function(){
      $('#menu-toggle-btn').on('click', function(){
        $('#side-container').toggleClass('span2 span1 hidden');
      });

      $('#content-container').on('click', '.continue-article', function(){
        if($(this).html() != 'Less...'){
          $(this).html('Less...');
          $(this).parentsUntil("#content").addClass('read');
        }
        else {
          $(this).html('Continue Reading...');
        }
        $(this).prev().prev('.article-summary').toggleClass('hidden');
        $(this).prev('.article-description').toggleClass('hidden');
      });

    };

  return {
    init : function(){
      $('#side-container').perfectScrollbar({wheelSpeed: 60});
      $('#content-container').perfectScrollbar({wheelSpeed: 60});
      binding();
    }
  };

})();
