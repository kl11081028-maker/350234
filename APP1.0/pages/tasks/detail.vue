<template>
  <view class="page">
    <view class="card glass-card">
      <view class="title-row">
        <text class="title">{{ task.title }}</text>
        <u-tag :text="priorityText(task.priority)" :type="priorityType(task.priority)" />
      </view>
      <view class="line">
        <u-icon name="file-text" size="30" color="#94a3b8" />
        <text class="label">描述</text>
      </view>
      <text class="desc">{{ task.desc || '暂无描述' }}</text>

      <view class="info">
        <view class="info-row">
          <u-icon name="grid" size="30" color="#94a3b8" />
          <text class="label">分类</text>
          <text class="value">{{ task.category || '未设置' }}</text>
        </view>
        <view class="info-row">
          <u-icon name="clock" size="30" color="#94a3b8" />
          <text class="label">番茄数量</text>
          <text class="value">{{ task.pomodoros || 0 }}</text>
        </view>
        <view class="info-row">
          <u-icon name="calendar" size="30" color="#94a3b8" />
          <text class="label">类型</text>
          <text class="value">{{ task.type || '未设置' }}</text>
        </view>
      </view>
    </view>

    <u-button type="primary" shape="circle" @click="goBack">返回</u-button>
  </view>
</template>

<script>
import { getTaskById } from '../../utils/storage';

export default {
  data() {
    return {
      task: {}
    };
  },
  onLoad(params) {
    if (params && params.data) {
      const { id } = JSON.parse(decodeURIComponent(params.data));
      const found = getTaskById(id);
      this.task = found || {};
    }
  },
  methods: {
    goBack() {
      uni.navigateBack();
    },
    priorityText(level) {
      const map = { high: '高', medium: '中', low: '低' };
      return map[level] || '中';
    },
    priorityType(level) {
      const map = { high: 'error', medium: 'warning', low: 'success' };
      return map[level] || 'warning';
    }
  }
};
</script>

<style lang="scss" scoped>
.page {
  padding: 32rpx;
  min-height: 100vh;
  background: linear-gradient(180deg, #fff6f4 0%, #f0f4ff 100%);
}

.card {
  padding: 28rpx 26rpx;
  color: #0f172a;
  margin-bottom: 32rpx;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  .title {
    font-size: 34rpx;
    font-weight: 700;
  }
}

.line {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 10rpx;
  .label {
    color: #475569;
    font-weight: 600;
  }
}

.desc {
  line-height: 1.7;
  color: #475569;
}

.info {
  margin-top: 24rpx;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 14rpx;
  .label {
    width: 140rpx;
    color: #475569;
    margin-left: 12rpx;
  }
  .value {
    color: #0f172a;
    font-weight: 600;
  }
}
</style>

