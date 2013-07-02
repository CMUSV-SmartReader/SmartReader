var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .factory('dbFactory', thermoreader.db)
  .controller('mainCtrl', thermoreader.mainCtrl)
  //.controller('manageCtrl', thermoreader.manageCtrl)
  .directive('ngBlur', function() {
    return function(scope, elem, attrs) {
      elem.bind('blur', function() {
        console.log(attrs);
        scope.$apply(attrs.ngBlur);
      });
    }
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
