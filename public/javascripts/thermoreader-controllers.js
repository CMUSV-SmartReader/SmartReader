var thermoreader = thermoreader || {};

/* The controller for Menu */
thermoreader.menuCtrl = function($scope, dbService) {
  $scope.allFeeds = dbService.getAllFeeds(false);
};

/* The controller for RSS Manage */
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

  $scope.$on('$viewContentLoaded', function(){
    $('#content-container').scrollTop(0);
  });

};

/* The base controller for all pages that display articles */
thermoreader.baseCtrl = function($scope, $routeParams, $http, dbService){

  // Some state variables
  $scope.orderRule = localStorage.hasOwnProperty('orderRule')? localStorage['orderRule']:"popular";
  $scope.isFeedPage = ($routeParams.feedId)? true:false;
  $scope.isEndOfFeed = false;
  $scope.viewMode = localStorage.hasOwnProperty('viewMode')? localStorage['viewMode']:"listMode"; // or "articleMode"

  $scope.animationMode = ($scope.viewMode == "listMode")?
    {enter: 'slideup-in', leave: 'slidedown-out'}:{show: 'slideleft-in'};
    //{show: 'slideleft-in', enter: 'slideup-in', leave: 'slidedown-out'}

  $scope.currentArticleIndex = 0; //orderedArticles[0];


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
    localStorage['orderRule'] = rule;
    $('.views select').blur();
  };

  $scope.setViewMode = function(mode){
    localStorage['viewMode'] = mode;
    if(mode == "articleMode"){
      $scope.animationMode = {show: 'slideleft-in'};
      $scope.isEndOfFeed = false;
    } else if(mode == "listMode"){
      $scope.animationMode = {enter: 'slideup-in', leave: 'slidedown-out'};
    }
    $('.views select').blur();
  };

  $scope.fetchData = function(isScrollTriggered){
    if(!$scope.isEndOfFeed && !$scope.isLoading && $scope.isFeedPage){
      if(!(isScrollTriggered ^ ($scope.viewMode == "listMode"))){
        var currentFeedLength = $scope.selectedFeed.articles.length;
        $scope.isLoading = true;
        dbService.getFeed($routeParams.feedId, true, function(feed){
          if(currentFeedLength == feed.articles.length){ $scope.isEndOfFeed = true; }
          else{
            if($scope.viewMode == "articleMode"){ $scope.currentArticleIndex += 1; }
          }
          $scope.isLoading = false;
        });
      }
    }
    //$scope.$apply();
  };

  $scope.keyTriggers = function(e){
    //console.log(e);
    switch(e.keyCode){
      case 39:
        if($scope.viewMode == "articleMode" && !$scope.isLoading){
          $scope.animationMode = {show: 'slideleft-in'};
          if($scope.currentArticleIndex+1 >= $scope.selectedFeed.articles.length){
            $scope.fetchData(false);
          } else { $scope.currentArticleIndex += 1; }
        }
        break;
      case 37:
        if($scope.viewMode == "articleMode"){
          $scope.animationMode = {show: 'slideright-in'};
          if($scope.currentArticleIndex < $scope.selectedFeed.articles.length){ $scope.isEndOfFeed = false; }
          if($scope.currentArticleIndex > 0){ $scope.currentArticleIndex -= 1; }
        }
        break;
      case 38:
        $('#content-container').scrollTop($('#content-container').scrollTop()-50);
        $('#content-container').perfectScrollbar('update');
        break;
      case 40:
        $('#content-container').scrollTop($('#content-container').scrollTop()+50);
        $('#content-container').perfectScrollbar('update');
        break;
    }
  };

  $scope.$on('$viewContentLoaded', function(){ $('#content-container').scrollTop(0); });

};

/* The controller for RSS feed page */
thermoreader.feedCtrl = function($scope, $routeParams, dbService){
  $scope.isLoading = (dbService.checkFeed($routeParams.feedId).articles.length == 0);
  $scope.selectedFeed = dbService.getFeed($routeParams.feedId, false, function(){ $scope.isLoading = false; });

  $scope.$on('$viewContentLoaded', function(){
    $http.put("/userfeed/"+$scope.selectedFeed.userFeedId+"/clear_update");
  });
};

/* The controller for Recommendation Page */
thermoreader.recommendationCtrl = function($scope, dbService){
  $scope.isLoading = true;
  $scope.selectedFeed = dbService.getRecommendations(function(){ $scope.isLoading = false; });

};

/* The controller for Social Network Page */
thermoreader.socialCtrl = function($scope, dbService){
  $scope.providers = dbService.getProviders();
  $scope.socialArticles = dbService.getSocialArticles();

  $scope.checkProvider = function(provider){
    return $scope.providers.hasOwnProperty(provider);
  };

};
