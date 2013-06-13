var thermoreader = thermoreader || {};

thermoreader.mainCtrl = function($scope, dbFactory) {
  $scope.allFeeds = dbFactory.allFeeds;
  $scope.selectedFeed = {};

  $scope.selectFeed = function(feed){
    console.log(feed.id);
    $scope.selectedFeed = { title: feed.title, articles: []};
    dbFactory.getFeed(feed.id).success(function(d){
      console.log(d);
      $scope.selectedFeed = { title: d.title, articles: []};
    });
  };

}

