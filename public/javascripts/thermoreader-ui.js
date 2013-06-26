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
    };

  return {
    init : function(){
      binding();
    }
  };

})();
