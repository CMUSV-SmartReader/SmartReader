var thermoreader = thermoreader || {};

thermoreader.ui = (function(){

  var
    binding = function(){

      $('#menu-toggle-btn').on('click', function(){
        $('#side-container').toggleClass('span2 span1 hidden');
      });

      $('#theme_change').on('click', function(){
        $(this).toggleClass('theme-inverse');
        $('.navbar-fixed-top').toggleClass('navbar-inverse');
        $('#side-container').toggleClass('side-inverse');
      });

      $('#main-feeds').on('click', 'a', function(){
        $('#main-feeds a').removeClass('active');
        $(this).addClass('active');
      });

      $('#side-container').perfectScrollbar({wheelSpeed: 60});
      $('#content-container').perfectScrollbar({wheelSpeed: 60});
    };

  return {
    init : function(){
      binding();
    }
  };

})();
