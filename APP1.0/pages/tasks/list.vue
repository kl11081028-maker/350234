<template>
  <view class="page">
    <view class="header">
      <text class="title">任务管理</text>
      <u-button size="mini" type="primary" @click="addMock">添加示例</u-button>
    </view>

    <view v-if="!tasks.length" class="empty">
      <u-icon name="inbox" size="64" color="#cbd5e1" />
      <text class="tips">还没有任务，点击右上角添加示例看看</text>
    </view>

    <view v-else>
      <view
        v-for="item in tasks"
        :key="item.id"
        class="card glass-card"
        @click="toDetail(item)"
      >
        <view class="row">
          <text class="task-title">{{ item.title }}</text>
          <u-tag :text="priorityText(item.priority)" :type="priorityType(item.priority)" />
        </view>
        <view class="row meta">
          <u-icon name="grid" size="28" color="#94a3b8" />
          <text class="meta-text">{{ item.category }}</text>
          <u-icon name="clock" size="28" color="#94a3b8" />
          <text class="meta-text">番茄 {{ item.pomodoros }}</text>
        </view>
        <text class="desc">{{ item.desc }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { loadTasks, upsertTask } from '../../utils/storage';

export default {
  data() {
    return {
      tasks: []
    };
  },
  onShow() {
    this.tasks = loadTasks();
  },
  methods: {
    priorityText(level) {
      const map = { high: '高', medium: '中', low: '低' };
      return map[level] || '中';
    },
    priorityType(level) {
      const map = { high: 'error', medium: 'warning', low: 'success' };
      return map[level] || 'warning';
    },
    addMock() {
      const sample = {
        id: `task-${Date.now()}`,
        title: '专注：产品文档完善',
        desc: '整理需求背景、里程碑、交付口径，准备评审材料。',
        priority: 'high',
        category: '产品',
        pomodoros: 3,
        type: 'work'
      };
      this.tasks = upsertTask(sample);
      uni.showToast({ title: '已添加示例', icon: 'none' });
    },
    toDetail(item) {
      const query = encodeURIComponent(JSON.stringify({ id: item.id }));
      uni.navigateTo({ url: `/pages/tasks/detail?data=${query}` });
    }
  }
};
</script>

<style lang="scss" scoped>
.page {
  padding: 32rpx;
  background: linear-gradient(180deg, #fff6f4 0%, #f2f6ff 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28rpx;
  .title {
    font-size: 36rpx;
    font-weight: 700;
    color: #1f2937;
  }
}

.empty {
  margin-top: 160rpx;
  text-align: center;
  color: #94a3b8;
  .tips {
    display: block;
    margin-top: 12rpx;
  }
}

.card {
  padding: 26rpx 24rpx;
  margin-bottom: 20rpx;
  color: #0f172a;
}

.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta {
  margin-top: 14rpx;
  gap: 12rpx;
  color: #64748b;
  .meta-text {
    margin-right: 16rpx;
  }
}

.task-title {
  font-size: 32rpx;
  font-weight: 600;
}

.desc {
  margin-top: 14rpx;
  color: #475569;
  line-height: 1.6;
}
</style>

