## 同步cookie和redis中购物车数据

```java
    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *                 如果cookie中的购物车不为空，此时直接放入redis中
         * 2. redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *                 如果cookie中的购物车不为空，
         *                      如果cookie中的某个商品在redis中存在，
         *                      则以cookie为主，删除redis中的，
         *                      把cookie中的商品直接覆盖redis中（参考京东）
         * 3. 同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        // 从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis为空，cookie不为空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */

                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);

                // 定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }

        }
    }
```

