var thermoreader = thermoreader || {};

thermoreader.mainCtrl = function($scope, dbFactory) {

  $scope.orderRule =
    localStorage.hasOwnProperty('orderRule')? localStorage['orderRule']:"popular";

  $scope.allFeeds = dbFactory.getAllFeeds(function(allFeeds){
    $scope.allFeeds = allFeeds;
  });

  $scope.selectedFeed = dbFactory.getRecommendations(function(recommendations) {
    $scope.selectedFeed = { name: "Recommendations", articles: recommendations };
  });

  $scope.selectFeed = function(feed) {
    $scope.selectedFeed = dbFactory.getFeed(feed.id, function(feed){
      $scope.selectedFeed = feed;
    });
  };

  $scope.getRecommendations = function() {
    $scope.selectedFeed = dbFactory.getRecommendations(function(recommendations){
      $scope.selectedFeed = { name: "Recommendations", articles: recommendations };
    });
  };

  $scope.expandArticle = function(article){
    article.expanded = !article.expanded;
    article.read = true;
  };


  $scope.setOrderRule = function(rule){
    $scope.orderRule = rule;
    localStorage['orderRule'] = rule;
  };

  $scope.$on('$viewContentLoaded', function(){
    $('#side-container').perfectScrollbar({wheelSpeed: 60});
    $('#content-container').perfectScrollbar({wheelSpeed: 60});
  });

};

thermoreader.manageCtrl = function($scope, dbFactory) {

};
