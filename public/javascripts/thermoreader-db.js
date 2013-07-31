var thermoreader = thermoreader || {};

thermoreader.dbService = function($http){
  this.userId = localStorage.hasOwnProperty('userId')? JSON.parse(localStorage['userId']):"";
  this.providers = {};
  this.socialArticles = [];
  this.categoryFeeds = [];
  this.recommendations = { name: "Recommendations", articles: [] };
  this.feedArticles = {};
  this.topicArticles = {};

  this.getRecommendations = function(callback){
    if(this.recommendations.articles.length == 0){
      var self = this;
      $http.get("/article/all_recommend").success(function(d) {
        console.log(d);
        self.recommendations.articles.length = 0;
        for(var i=0; i<d.length; ++i){
          self.recommendations.articles.push( new thermoreader.model.article(
            d[i].id, d[i].title, d[i].author, d[i].publishDate,
            "Recommendations", d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), false
          ));
        };
        if(callback){ callback(self.recommendations); }
      });
    }
    return this.recommendations;
  };

  this.getAllFeeds = function(isReload, callback){
    if(this.categoryFeeds.length == 0 || isReload){
      var self = this;
      $http.get("/userfeed/all").success(function(d) {
        var userFeedInfo = {};
        for(var i=0; i<d.length; ++i){ userFeedInfo[d[i].id] = d[i]; }
        $http.get("/categories").success(function(d) {
          self.categoryFeeds.length = 0;
          for(var i=0; i<d.length; ++i){
            self.categoryFeeds.push({ id: d[i].id, name : d[i].name, feeds : [] });
            for(var j=0; j<d[i].userFeedsInfos.length; ++j){
              var userFeedId = d[i].userFeedsInfos[j].userFeedId;
              var feed = new thermoreader.model.feed(
                d[i].userFeedsInfos[j].feedId, d[i].userFeedsInfos[j].feedTitle,
                userFeedId, (new Date()), (new Date()), [],
                userFeedInfo[userFeedId].hasUpdate, userFeedInfo[userFeedId].order, userFeedInfo[userFeedId].popularity
              );
              self.categoryFeeds[i].feeds.push(feed);
              self.feedArticles[d[i].userFeedsInfos[j]["feedId"]] = feed;
            }
          }
          console.log(self.categoryFeeds);
          if(callback){ callback(self.categoryFeeds); }
        });
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
    this.feedArticles[feedId] = this.feedArticles[feedId] || {};
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

  this.getProviders = function(){
    var self = this;
    $http.get('/social/all_provider').success(function(d){
      for(var i=0; i<d.length; ++i){
        self.providers[d[i].provider] = d[i].id;
      }
      self.getSocialArticles();
    });
    return this.providers;
  };

  this.getSocialArticles = function(isReload){
    var self = this;
    for(var provider in this.providers){
      $http.get('social/'+this.providers[provider]+'/articles').success(function(d){
        // console.log(d);
        self.socialArticles.length = 0;
        for(var i=0; i<d.length; ++i){
          self.socialArticles.push( new thermoreader.model.article(
            d[i].id, d[i].desc.slice(0, 12), d[i].author || d[i].feed.title, d[i].publishDate,
            provider, d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), d[i].isRead
          ));
        }
      });
    }
    return this.socialArticles;
  };

  this.getTopicArticles = function(topicName, callback){
    var self = this;
    $http.get('/category_articles').success(function(d){
      console.log(d);
      self.topicArticles[topicName].length = 0;
      for(var i=0; i<d.length; ++i){
        self.topicArticles[topicName].articles.push( new thermoreader.model.article(
          d[i].id, d[i].title, d[i].author || d[i].feed.title, d[i].publishDate,
          d[i].feed.title, d[i].desc.slice(0, 48), d[i].desc,
          d[i].link, Math.floor(Math.random()*5+1), d[i].isRead
        ));
      }
      if(callback){ callback(); }
    });
    this.topicArticles[topicName] = this.topicArticles[topicName] || { name: topicName, articles: [] };
    return this.topicArticles[topicName];
  }

};
