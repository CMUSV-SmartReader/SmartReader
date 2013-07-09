var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .factory('dbFactory', thermoreader.db)
  .controller('mainCtrl', thermoreader.mainCtrl)
  .directive('ngBlur', function() {
    return function(scope, elem, attrs) {
      elem.bind('blur', function() { scope.$apply(attrs.ngBlur); });
    };
  })
  .directive('ngScrollend', function() {
    return function(scope, elem, attrs) {
      elem.bind('scroll', function(){
        if(elem[0].scrollTop + elem[0].offsetHeight >= elem[0].scrollHeight){
          scope.$apply(attrs.ngScrollend);
        }
      });
    };
  })
  .filter('prettyDate', function() {
    return function(time) {
      return prettyDate(new Date(time));
    };
  })
  .config(function($routeProvider) {
  	$routeProvider
  	//.when("/home", {controller: "mainCtrl", templateUrl: "/assets/templates/main.html"})
    .when("/:pageName", {controller: "mainCtrl", templateUrl: "/assets/templates/main.html"})
    .when("/:pageName/:feedId", {controller: "mainCtrl", templateUrl: "/assets/templates/main.html"})
    .otherwise({redirectTo: '/home'});
  }
);


$(document).on('ready', function(){
  thermoreader.ui.init();
});

