var thermoreader = thermoreader || {};

thermoreader.db = function($http){

	var
    userId =
      localStorage.hasOwnProperty('userId')? JSON.parse(localStorage['userId']):"",
    categoryFeeds =
      localStorage.hasOwnProperty('categoryFeeds')? JSON.parse(localStorage['categoryFeeds']):[],
    recommendations = [],
    feedArticles = {},

    getRecommendations = function(callback){
      $http.get("/article/all_recommend").success(function(d) {
        console.log(d);
        recommendations = [];
        for(var i=0; i<d.length; ++i){
          recommendations.push( new thermoreader.model.article(
            d[i].title, d[i].author, d[i].publishDate,
            "Recommendations", d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), false
          ));
        };
        callback(recommendations);
      });
      return recommendations;
    },

    getAllFeeds = function(callback){
      $http.get("/categories").success(function(data) {
        console.log(data);
        for(var i=0; i<data.length; ++i){
          categoryFeeds.push({ name : data[i].name, feeds : [] });
          for(var j=0; j<data[i].userFeedsInfos.length; ++j){
            for(var key in data[i].userFeedsInfos[j]){
              categoryFeeds[i].feeds.push(
                new thermoreader.model.feed( key, data[i].userFeedsInfos[j][key] )
              );
              feedArticles[key] = {
                name: data[i].userFeedsInfos[j][key], articles: []
              };
        } } }
        callback(categoryFeeds);
        //localStorage['categoryFeeds'] = JSON.stringify(categoryFeeds);
      });
      return categoryFeeds;
    },

    getFeed = function(feedId, callback){
      $http.get("/feed/"+feedId).success(function(d){
        console.log(d);
        feedArticles[feedId].articles = []; // clear cache
        for(var i=0; i<d.length; ++i){
          feedArticles[feedId].articles.push( new thermoreader.model.article(
            d[i].title, d[i].author || feedArticles[feedId].name, d[i].publishDate,
            feedArticles[feedId].name, d[i].desc.slice(0, 48), d[i].desc,
            d[i].link, Math.floor(Math.random()*5+1), false
          ));
        };
        callback(feedArticles[feedId]);
      });
      return feedArticles[feedId];
    },

    getDuplicates = function(articleId){
      $http.get("/article/"+articleId+"/dup").success(function(d){
        console.log(d);
      });
    };

	return {
    getRecommendations : getRecommendations,
    getAllFeeds : getAllFeeds,
    getFeed : getFeed
  };

};
