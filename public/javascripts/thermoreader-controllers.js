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


  //$scope.title = dbFactory.currentFeed.name;
  //$scope.articles = dbFactory.currentFeed.articles;
}

/*function menuCtrl($scope, $http) {
  $scope.categories = [];

  smartreader.core.putCategories = function(categories){
    $scope.categories = categories;
    //$scope.$apply();
  }

  smartreader.core.loadCategories = function(){
    var userCategories = [];
    $http.get("/categories").success(function(data) {
      console.log(data);
      for(var i=0; i<data.length; ++i){
        userCategories.push({
          name : data[i].name,
          feeds : []
        });
        for(var j=0; j<data[i].userFeeds.length; ++j){
          userCategories[i].feeds.push(
            new smartreader.feed(data[i].userFeeds[j].id, data[i].userFeeds[j].name)
          );
        }
        smartreader.core.putCategories(userCategories);
      }
    });
  }

}

function function($scope) {
  $scope.title = "";
  $scope.articles = [];

  smartreader.core.putTitle = function(title){
    $scope.title = title;
    $scope.$apply();
  }

  smartreader.core.putArticles = function(articles){
    $scope.articles = articles;
    $scope.$apply();
  }


}*/
