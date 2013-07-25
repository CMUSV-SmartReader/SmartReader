var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .service('dbService', thermoreader.dbService)
  .controller('menuCtrl', thermoreader.menuCtrl)
  .controller('manageCtrl', thermoreader.manageCtrl)
  .controller('feedCtrl', thermoreader.feedCtrl)
  .directive('ngBlur', function() {
    return function(scope, elem, attrs) {
      elem.bind('blur', function() { scope.$apply(attrs.ngBlur); });
    };
  })
  .directive('ngScrollend', function() {
    return function(scope, elem, attrs) {
      $('#content-container').unbind('scroll');
      $('#content-container').bind('scroll', function(){
        if($('#content-container')[0].scrollTop + $('#content-container')[0].offsetHeight >= $('#content-container')[0].scrollHeight){
          //console.log("scroll");
          scope.$apply(attrs.ngScrollend);
        }
      });
    };
  })
  .directive('ngGlobalkeydown', function($document, $parse) {
    return function(scope, elem, attrs) {
      $document.unbind('keydown');
      $document.bind('keydown',function(event){
        //console.log("keydown");
        scope.$apply(function() {
          $parse(attrs.ngGlobalkeydown)(scope, { $event: event });
        });
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
    .when("/recommendation", {controller: "feedCtrl", templateUrl: "/assets/templates/feed.html"})
    .when("/discover", {controller: "discoverCtrl", templateUrl: "/assets/templates/discover.html"})
    .when("/social", {controller: "socialCtrl", templateUrl: "/assets/templates/social.html"})
    .when("/manage", {controller: "manageCtrl", templateUrl: "/assets/templates/manage.html"})
    .when("/feed/:feedId", {controller: "feedCtrl", templateUrl: "/assets/templates/feed.html"})
    .otherwise({redirectTo: '/discover'});
  }
);


$(document).on('ready', function(){
  thermoreader.ui.init();
});

