var thermoreader = thermoreader || {};

thermoreader.dbService = function($http){
  this.userId = localStorage.hasOwnProperty('userId')? JSON.parse(localStorage['userId']):"";
  this.categoryFeeds = [];
  this.recommendations = [];
  this.feedArticles = {};

  this.getRecommendations = function(callback){
    var self = this;
    $http.get("/article/all_recommend").success(function(d) {
      console.log(d);
      self.recommendations.length = 0;
      for(var i=0; i<d.length; ++i){
        self.recommendations.push( new thermoreader.model.article(
          d[i].id, d[i].title, d[i].author, d[i].publishDate,
          "Recommendations", d[i].desc.slice(0, 48), d[i].desc,
          d[i].link, Math.floor(Math.random()*5+1), false
        ));
      };
      if(callback){ callback(self.recommendations); }
    });
    return this.recommendations;
  };

  this.getAllFeeds = function(isReload, callback){
    if(this.categoryFeeds.length == 0 || isReload){
      var self = this;
      $http.get("/categories").success(function(d) {
        console.log(d);
        self.categoryFeeds.length = 0;
        for(var i=0; i<d.length; ++i){
          self.categoryFeeds.push({ id: d[i].id, name : d[i].name, feeds : [] });
          for(var j=0; j<d[i].userFeedsInfos.length; ++j){
            var feed = new thermoreader.model.feed(
              d[i].userFeedsInfos[j]["feedId"],
              d[i].userFeedsInfos[j]["feedTitle"],
              d[i].userFeedsInfos[j]["userFeedId"],
              (new Date()), (new Date()), []
            );
            self.categoryFeeds[i].feeds.push(feed);
            self.feedArticles[d[i].userFeedsInfos[j]["feedId"]] = feed;
          }
        }
        if(callback){ callback(self.categoryFeeds); }
      });
    }
    return this.categoryFeeds;
  };

  this.checkFeed = function(feedId){
    return this.feedArticles[feedId];
  };

  this.getFeed = function(feedId, isScroll, callback){
    if(this.feedArticles[feedId].articles.length == 0 || isScroll){
      var self = this;
      $http.get("/feed/"+feedId+"?publishDate="+self.feedArticles[feedId].earliest.getTime()).success(function(d){
        console.log(d);
        //this.feedArticles[feedId].articles = []; // clear cache
        for(var i=0; i<d.length; ++i){
          self.feedArticles[feedId].articles.push( new thermoreader.model.article(
            d[i].id, d[i].title, d[i].author || self.feedArticles[feedId].name, d[i].publishDate,
            self.feedArticles[feedId].name, d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), d[i].isRead
          ));
          // Update latest and earliest data
          if(self.feedArticles[feedId].latest < (new Date(d[i].publishDate))){
            self.feedArticles[feedId].latest = (new Date(d[i].publishDate));
          }
          if(self.feedArticles[feedId].earliest > (new Date(d[i].publishDate))){
            self.feedArticles[feedId].earliest = (new Date(d[i].publishDate));
          }
        };
        if(callback){ callback(self.feedArticles[feedId]); }
      });
    }
    return this.feedArticles[feedId];
  };

  this.getDuplicates = function(articleId, callback){
    $http.get("/article/"+articleId+"/dup").success(function(d){
      var duplicates = [];
      for(var i=0; i<d.length; ++i){
        duplicates.push( new thermoreader.model.article(
          d[i].id, d[i].title, d[i].author || d[i].feed.title, d[i].publishDate,
          d[i].feed.title, d[i].desc.slice(0, 48), d[i].desc,
          d[i].link, Math.floor(Math.random()*5+1), d[i].isRead
        ));
      }
      if(callback){ callback(duplicates); }
    });
  };

};
