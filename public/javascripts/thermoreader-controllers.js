var thermoreader = thermoreader || {};

thermoreader.mainCtrl = function($scope, dbFactory) {

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

};

thermoreader.manageCtrl = function($scope, dbFactory) {

  $scope.allFeeds = dbFactory.getAllFeeds(function(allFeeds) {
    $scope.allFeeds = allFeeds;
  });

};