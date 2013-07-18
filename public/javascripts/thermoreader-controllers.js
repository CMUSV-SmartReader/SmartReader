var thermoreader = thermoreader || {};

thermoreader.menuCtrl = function($scope, dbService) {
  $scope.allFeeds = dbService.getAllFeeds(false);
};

thermoreader.manageCtrl = function($scope, $http, dbService){
  $scope.allFeeds = dbService.getAllFeeds(false);

  $scope.addNewCategory = function(){
    var categoryName = window.prompt("Please input category name", "Untitiled Category");
    if(categoryName != null && $.trim(categoryName) != ""){
      $http.post("/category/create", { data: categoryName }).success(function(d) {
        console.log("successful adding new category");
        dbService.getAllFeeds(true);
      });
    }
  }

  $scope.deleteCategory = function(category){
    console.log(category);
    $http.delete("/category/" + category.id).success(function() {
      console.log("successful delete");
      dbService.getAllFeeds(true);
    });
  }

  $scope.deleteUserFeed = function(category, feed) {
    $http.delete("/category/" + category.id + "/" + feed.userFeedId).success(function() {
      console.log("successful delete");
      dbService.getAllFeeds(true);
    });
  };

  $scope.addNewFeed = function(category, feedURL) {
    $scope.manageFeedURL = "";
    $http.post("/category/" + category.id + "/add_feed", {
      data: feedURL // ex. http://rss.sina.com.cn/news/allnews/tech.xml
    }).success(function(d) {
      console.log("successful adding new feed");
      dbService.getAllFeeds(true);
    });
  };

};

thermoreader.feedCtrl = function($scope, $routeParams, $http, dbService){

  $scope.orderRule = localStorage.hasOwnProperty('orderRule')? localStorage['orderRule']:"popular";
  $scope.isFeedPage = ($routeParams.feedId)? true:false;
  $scope.isLoading = ($scope.isFeedPage)? (dbService.checkFeed($routeParams.feedId).articles.length == 0):true;
  $scope.isEndOfFeed = false;
  $scope.viewMode = "listMode"; // or "articleMode"

  // If the page is to display a feed
  $scope.selectedFeed = ($scope.isFeedPage)?
    dbService.getFeed($routeParams.feedId, false, function(){ $scope.isLoading = false; }):
    dbService.getRecommendations(function(){ $scope.isLoading = false; });

  // Feed Page Functions
  $scope.expandArticle = function(article){
    article.expanded = !article.expanded;
    // Fetch the articles in this feed
    if(article.expanded && !article.read){
      $http.put("/article/"+article.id+"/read").success( function(){ article.read = true; });
      if($scope.isFeedPage){ $http.put("/userfeed/"+$scope.selectedFeed.userFeedId+"/inc_popularity"); }
    }

    // Temporary disable duplicates before underlying service is reasonably working
    // dbService.getDuplicates(article.id, function(d){
    //   console.log(d);
    //   if(d.length > 0){ article.duplicates = [new thermoreader.model.article(
    //     article.id, article.title, article.author, article.date,
    //     article.feedName, article.summary, article.description,
    //     article.link, article.popular, article.read
    //   )].concat(d); }
    // });
  };

  $scope.replaceArticle = function(article, dupArticle){
    article.title = dupArticle.title;
    article.link = dupArticle.link;
    article.date = dupArticle.date;
    article.author = dupArticle.author;
    article.feedName = dupArticle.feedName;
    article.description = dupArticle.description;
  };

  $scope.setOrderRule = function(rule){
    $scope.orderRule = rule;
    localStorage['orderRule'] = rule;
  };

  $scope.fetchData = function(){
    if(!$scope.isEndOfFeed && !$scope.isLoading && $scope.isFeedPage){
      $scope.isLoading = true;
      dbService.getFeed($routeParams.feedId, true, function(feed){
        if($scope.selectedFeed.length == feed.length){ $scope.isEndOfFeed = true; }
        $scope.isLoading = false;
      });
    }
    //$scope.$apply();
  };

  $scope.$on('$viewContentLoaded', function(){
    $('#content-container').scrollTop(0);
    if($scope.isFeedPage){ $http.put("/userfeed/"+$scope.selectedFeed.userFeedId+"/clear_update"); }
  });
};

// Hotkeys Binding
// angular.element($document).bind("keyup", function(event) {
//   if (event.which === 191) {
//     $('#hotkeys').modal({
//       backdropFade: true,
//       dialogFade:true
//     });
//   }
// });
