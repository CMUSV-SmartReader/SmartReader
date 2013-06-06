var smartreader = smartreader || {};

smartreader.core = (function($http){

  var
    init = function(){
      $.getJSON("/categories").success(function(data) {
        console.log(data);
        smartreader.putCategories(data);
      });
    };

  return {
    init : init
  };

})($http);
