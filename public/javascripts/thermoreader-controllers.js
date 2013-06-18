var thermoreader = thermoreader || {};

thermoreader.mainCtrl = function($scope, dbFactory) {

  $scope.allFeeds = dbFactory.fetchAllFeeds(function(){
    $scope.allFeeds = dbFactory.getAllFeeds();
  });

  $scope.selectedFeed = {};

  $scope.selectFeed = function(feed){
    console.log(feed.id);
    dbFactory.getFeed(feed.id, function(feed){
      $scope.selectedFeed = feed;
    });
  };

}

