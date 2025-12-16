<template>
  <view class="page">
    <image class="bg" :src="bgUrl" mode="aspectFill" />
    <view class="overlay" />
    <view class="top-bar">
      <text class="logo">FANQIE</text>
      <u-icon name="list" size="48" color="#fff" @click="goTaskList" />
    </view>

    <view class="clock-card glass-card">
      <view class="chip-row">
        <view
          class="chip"
          :class="{ active: mode === 'work' }"
          @click="switchMode('work')"
        >
          <u-icon name="clock" size="32" color="#ffefe9" />
          <text>工作 25:00</text>
        </view>
        <view
          class="chip"
          :class="{ active: mode === 'rest' }"
          @click="switchMode('rest')"
        >
          <u-icon name="heart" size="32" color="#e8fff5" />
          <text>休息 05:00</text>
        </view>
      </view>

      <view class="timer-wrap">
        <view class="ring">
          <view class="halo"></view>
          <text class="time">{{ displayTime }}</text>
          <text class="mode-label">{{ modeLabel }}</text>
        </view>
      </view>

      <view class="control-row">
        <u-button
          class="btn"
          type="info"
          shape="circle"
          size="large"
          plain
          @click="resetTimer"
        >
          <u-icon name="reload" size="34" />
        </u-button>
        <u-button
          class="btn main"
          type="error"
          shape="circle"
          size="large"
          ripple
          @click="toggleTimer"
        >
          <u-icon :name="isRunning ? 'pause-circle' : 'play-circle'" size="48" />
        </u-button>
        <u-button
          class="btn"
          type="success"
          shape="circle"
          size="large"
          plain
          @click="completeCycle"
        >
          <u-icon name="checkbox-mark" size="34" />
        </u-button>
      </view>

      <view class="status-row">
        <text>今日完成番茄：{{ cycles }}</text>
        <text class="muted">当前阶段：{{ modeLabel }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import { loadTasks } from '../../utils/storage';

export default {
  data() {
    return {
      workSeconds: 25 * 60,
      restSeconds: 5 * 60,
      secondsLeft: 25 * 60,
      timer: null,
      mode: 'work',
      cycles: 0,
      bgUrl:
        'https://images.unsplash.com/photo-1520880867055-1e30d1cb001c?auto=format&fit=crop&w=1200&q=80'
    };
  },
  computed: {
    isRunning() {
      return !!this.timer;
    },
    displayTime() {
      const m = Math.floor(this.secondsLeft / 60)
        .toString()
        .padStart(2, '0');
      const s = (this.secondsLeft % 60).toString().padStart(2, '0');
      return `${m}:${s}`;
    },
    modeLabel() {
      return this.mode === 'work' ? '专注' : '休息';
    }
  },
  onShow() {
    loadTasks(); // 触发本地存储初始化
  },
  beforeDestroy() {
    this.clearTimer();
  },
  methods: {
    toggleTimer() {
      if (this.timer) {
        this.clearTimer();
        return;
      }
      this.timer = setInterval(() => {
        if (this.secondsLeft > 0) {
          this.secondsLeft -= 1;
        } else {
          this.onFinish();
        }
      }, 1000);
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    resetTimer() {
      this.clearTimer();
      this.secondsLeft = this.mode === 'work' ? this.workSeconds : this.restSeconds;
    },
    switchMode(target) {
      if (this.mode === target) return;
      this.mode = target;
      this.resetTimer();
    },
    onFinish() {
      this.clearTimer();
      if (this.mode === 'work') {
        this.mode = 'rest';
        this.secondsLeft = this.restSeconds;
        uni.showToast({ title: '工作完成，休息一下~', icon: 'none' });
      } else {
        this.mode = 'work';
        this.secondsLeft = this.workSeconds;
        this.cycles += 1;
        uni.vibrateLong();
        uni.showToast({ title: '休息结束，继续加油！', icon: 'none' });
      }
    },
    completeCycle() {
      this.cycles += 1;
      this.mode = 'work';
      this.resetTimer();
      uni.showToast({ title: '已记录一个番茄', icon: 'success' });
    },
    goTaskList() {
      uni.navigateTo({ url: '/pages/tasks/list' });
    }
  }
};
</script>

<style lang="scss" scoped>
.page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  padding: 40rpx 32rpx 80rpx;
}

.bg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 95, 82, 0.72), rgba(17, 24, 39, 0.6));
}

.top-bar {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  color: #fff;
  font-weight: 700;
  letter-spacing: 4rpx;
}

.clock-card {
  position: relative;
  margin-top: 60rpx;
  padding: 40rpx 32rpx;
  color: #fff;
}

.chip-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 32rpx;
  .chip {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12rpx;
    padding: 20rpx 22rpx;
    border-radius: 20rpx;
    background: rgba(255, 255, 255, 0.08);
    color: #fdf2f2;
    transition: transform 0.15s;
    &:active {
      transform: scale(0.98);
    }
    & + .chip {
      margin-left: 16rpx;
    }
  }
  .active {
    background: rgba(255, 255, 255, 0.22);
    box-shadow: 0 10rpx 28rpx rgba(255, 255, 255, 0.12);
  }
}

.timer-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 30rpx 0 46rpx;
}

.ring {
  position: relative;
  width: 360rpx;
  height: 360rpx;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.26), rgba(255, 255, 255, 0.06));
  box-shadow: 0 24rpx 68rpx rgba(255, 95, 82, 0.35);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.halo {
  position: absolute;
  inset: 12rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(255, 255, 255, 0.18);
}

.time {
  font-size: 76rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
}

.mode-label {
  margin-top: 8rpx;
  font-size: 28rpx;
  opacity: 0.8;
}

.control-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28rpx;
  .btn {
    flex: 1;
    margin: 0 8rpx;
  }
  .main {
    transform: scale(1.04);
    box-shadow: 0 16rpx 32rpx rgba(255, 95, 82, 0.35);
  }
}

.status-row {
  display: flex;
  justify-content: space-between;
  color: rgba(255, 255, 255, 0.9);
  font-size: 26rpx;
  .muted {
    opacity: 0.78;
  }
}
</style>

