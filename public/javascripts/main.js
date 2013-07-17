var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .service('dbService', thermoreader.dbService)
  .controller('menuCtrl', thermoreader.menuCtrl)
  .controller('manageCtrl', thermoreader.manageCtrl)
  .controller('feedCtrl', thermoreader.feedCtrl)
  .controller('mainCtrl', thermoreader.mainCtrl)
  .directive('ngBlur', function() {
    return function(scope, elem, attrs) {
      elem.bind('blur', function() { scope.$apply(attrs.ngBlur); });
    };
  })
  .directive('ngScrollend', function() {
    return function(scope, elem, attrs) {
      console.log($('#content-container'));
      $('#content-container').bind('scroll', function(){
        if($('#content-container')[0].scrollTop + $('#content-container')[0].offsetHeight >= $('#content-container')[0].scrollHeight){
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
  	//.when("/home", {controller: "mainCtrl", templateUrl: "/assets/templates/feed.html"})
    .when("/manage", {controller: "manageCtrl", templateUrl: "/assets/templates/manage.html"})
    .when("/feed/:feedId", {controller: "feedCtrl", templateUrl: "/assets/templates/feed.html"})
    //.when("/:pageName", {controller: "mainCtrl", templateUrl: "/assets/templates/feed.html"})
    //.when("/:pageName/:feedId", {controller: "mainCtrl", templateUrl: "/assets/templates/feed.html"})
    .otherwise({redirectTo: '/home'});
  }
);


$(document).on('ready', function(){
  thermoreader.ui.init();
});

