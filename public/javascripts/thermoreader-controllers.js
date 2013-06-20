var thermoreader = thermoreader || {};

thermoreader.mainCtrl = function($scope, dbFactory) {

  $scope.allFeeds = dbFactory.getAllFeeds(function(allFeeds){
    $scope.allFeeds = allFeeds;
  });

  $scope.selectedFeed = dbFactory.getRecommendations(function(recommendations){
    $scope.selectedFeed = { name: "Recommendations", articles: recommendations };
    // $scope.selectedFeed.articles.sort(function(a, b){
    //   return (a.popular>b.popular);
    // });
  });

  $scope.selectFeed = function(feed){
    $scope.selectedFeed = dbFactory.getFeed(feed.id, function(feed){
      $scope.selectedFeed = feed;
      // $scope.selectedFeed.article = $scope.selectedFeed.articles.sort(function(a, b){
      //   return (a.popular>b.popular);
      // });
    });
  };

  $scope.getRecommendations = function(){
    $scope.selectedFeed = dbFactory.getRecommendations(function(recommendations){
      $scope.selectedFeed = { name: "Recommendations", articles: recommendations };
      // $scope.selectedFeed.article = $scope.selectedFeed.articles.sort(function(a, b){
      //   return (a.popular>b.popular);
      // });
    });
  }

  $scope.expandArticle = function(article){
    article.expanded = !article.expanded;
    article.read = true;
  }

}

