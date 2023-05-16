package org.infinispan.server.resp;

import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.api.sync.RedisCommands;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * RESP List commands testing
 *
 * @since 15.0
 */
@Test(groups = "functional", testName = "server.resp.RespListCommandsTest")
public class RespListCommandsTest extends SingleNodeRespBaseTest {

   public void testRPUSH() {
      RedisCommands<String, String> redis = redisConnection.sync();
      long result = redis.rpush("people", "tristan");
      assertThat(result).isEqualTo(1);

      result = redis.rpush("people", "william");
      assertThat(result).isEqualTo(2);

      result = redis.rpush("people", "william", "jose", "pedro");
      assertThat(result).isEqualTo(5);

      // Set a String Command
      redis.set("leads", "tristan");

      // Push on an existing key that contains a String, not a Collection!
      assertThatThrownBy(() -> {
               redis.rpush("leads", "william");
      }).isInstanceOf(RedisCommandExecutionException.class)
            .hasMessageContaining("ERRWRONGTYPE");
   }

   public void testRPUSHX() {
      RedisCommands<String, String> redis = redisConnection.sync();
      long result = redis.rpushx("noexisting", "doraemon", "son goku");
      assertThat(result).isEqualTo(0);

      result = redis.rpush("existing", "tristan");
      assertThat(result).isEqualTo(1);

      result = redis.rpushx("existing", "william", "jose");
      assertThat(result).isEqualTo(3);

      // Set a String Command
      redis.set("leads", "tristan");

      // RPUSHX on an existing key that contains a String, not a Collection!
      assertThatThrownBy(() -> {
         redis.rpushx("leads", "william");
      }).isInstanceOf(RedisCommandExecutionException.class)
            .hasMessageContaining("ERRWRONGTYPE");
   }

   public void testLPUSH() {
      RedisCommands<String, String> redis = redisConnection.sync();
      long result = redis.lpush("people", "tristan");
      assertThat(result).isEqualTo(1);

      result = redis.lpush("people", "william");
      assertThat(result).isEqualTo(2);

      result = redis.lpush("people", "william", "jose", "pedro");
      assertThat(result).isEqualTo(5);

      // Set a String Command
      redis.set("leads", "tristan");

      // Push on an existing key that contains a String, not a Collection!
      assertThatThrownBy(() -> {
         redis.lpush("leads", "william");
      }).isInstanceOf(RedisCommandExecutionException.class)
            .hasMessageContaining("ERRWRONGTYPE");
   }

   public void testLPUSHX() {
      RedisCommands<String, String> redis = redisConnection.sync();
      long result = redis.lpushx("noexisting", "doraemon", "son goku");
      assertThat(result).isEqualTo(0);

      result = redis.rpush("existing", "tristan");
      assertThat(result).isEqualTo(1);

      result = redis.lpushx("existing", "william", "jose");
      assertThat(result).isEqualTo(3);

      // Set a String Command
      redis.set("leads", "tristan");

      // LPUSHX on an existing key that contains a String, not a Collection!
      assertThatThrownBy(() -> {
         redis.lpushx("leads", "william");
      }).isInstanceOf(RedisCommandExecutionException.class)
            .hasMessageContaining("ERRWRONGTYPE");
   }

   public void testLINDEX() {
      RedisCommands<String, String> redis = redisConnection.sync();
      assertThat(redis.lindex("noexisting", 10)).isNull();

      redis.rpush("leads", "tristan");
      assertThat(redis.lindex("leads", 0)).isEqualTo("tristan");
      assertThat(redis.lindex("leads", -1)).isEqualTo("tristan");
      assertThat(redis.lindex("leads", 1)).isNull();
      assertThat(redis.lindex("leads", -2)).isNull();

      redis.rpush("leads", "william", "jose", "ryan", "pedro", "vittorio");
      // size 6: ["tristan", "william", "jose", "ryan", "pedro", "vittorio"]
      assertThat(redis.lindex("leads", 1)).isEqualTo("william");
      assertThat(redis.lindex("leads", -1)).isEqualTo("vittorio");
      assertThat(redis.lindex("leads", -6)).isEqualTo("tristan");
      assertThat(redis.lindex("leads", 3)).isEqualTo("ryan");
      assertThat(redis.lindex("leads", -3)).isEqualTo("ryan");
      assertThat(redis.lindex("leads", 6)).isNull();
      assertThat(redis.lindex("leads", -7)).isNull();

      // Set a String Command
      redis.set("another", "tristan");

      // LINDEX on an existing key that contains a String, not a Collection!
      assertThatThrownBy(() -> {
         redis.lindex("another", 1);
      }).isInstanceOf(RedisCommandExecutionException.class)
            .hasMessageContaining("ERRWRONGTYPE");
   }
}
