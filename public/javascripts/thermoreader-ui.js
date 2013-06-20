var thermoreader = thermoreader || {};

thermoreader.ui = (function(){

  var
    binding = function(){
      $('#menu-toggle-btn').on('click', function(){
        $('#side-container').toggleClass('span2 span1 hidden');
      });
    };

  return {
    init : function(){
      binding();
    }
  };

})();
