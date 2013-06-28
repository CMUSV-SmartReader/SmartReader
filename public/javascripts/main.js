var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .factory('dbFactory', thermoreader.db)
  .controller('mainCtrl', thermoreader.mainCtrl)
  .controller('manageCtrl', thermoreader.manageCtrl)
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
  	.when("/", {controller: "mainCtrl", templateUrl: "/assets/templates/main.html"})
    .when("/manage", {controller: "manageCtrl", templateUrl: "/assets/templates/manage.html"})
    .when("/:feedId", {controller: "mainCtrl", templateUrl: "/assets/templates/main.html"})
    .when("/:feedId/:articleId", {controller: "mainCtrl", templateUrl: "/assets/templates/main.html"})
  }
);


$(document).on('ready', function(){
  thermoreader.ui.init();
});
