<template>
  <view class="app-root">
    <slot />
  </view>
</template>

<script>
export default {
  onLaunch() {
    // 预热本地存储
    const tasks = uni.getStorageSync('tasks');
    if (!tasks || !Array.isArray(tasks)) {
      uni.setStorageSync('tasks', []);
    }
  }
};
</script>

<style lang="scss">
@import "uview-ui/index.scss";
@import "./uni.scss";

.app-root {
  min-height: 100vh;
  background: linear-gradient(180deg, #ff7363 0%, #ff9a7a 100%);
}
</style>

