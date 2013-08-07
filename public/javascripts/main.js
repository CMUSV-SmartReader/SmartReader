var thermoreader = thermoreader || {};

thermoreader.instance = angular.module('thermoReader', [])
  .service('dbService', thermoreader.dbService)
  .controller('menuCtrl', thermoreader.menuCtrl)
  .controller('manageCtrl', thermoreader.manageCtrl)
  .controller('baseCtrl', thermoreader.baseCtrl)
  .controller('feedCtrl', thermoreader.feedCtrl)
  .controller('socialCtrl', thermoreader.socialCtrl)
  .controller('topicCtrl', thermoreader.topicCtrl)
  .controller('recommendationCtrl', thermoreader.recommendationCtrl)
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
    .when("/recommendation", {controller: "baseCtrl", templateUrl: "/assets/templates/recommendation.html"})
    .when("/discover", {controller: "baseCtrl", templateUrl: "/assets/templates/discover.html"})
    .when("/social", {controller: "baseCtrl", templateUrl: "/assets/templates/social.html"})
    .when("/manage", {controller: "manageCtrl", templateUrl: "/assets/templates/manage.html"})
    .when("/manage/:fnName", {controller: "manageCtrl", templateUrl: "/assets/templates/manage.html"})
    .when("/topic/:topicName", {controller: "baseCtrl", templateUrl: "/assets/templates/topic.html"})
    .when("/feed/:feedId", {controller: "baseCtrl", templateUrl: "/assets/templates/feed.html"})
    .otherwise({redirectTo: '/discover'});
  }
);


$(document).on('ready', function(){
  thermoreader.ui.init();
});

