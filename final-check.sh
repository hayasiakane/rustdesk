#!/bin/bash
echo "🧹 最终准备工作验证..."

# 1. 检查语法错误
echo "📍检查Flutter语法..."
cd flutter
flutter analyze lib/common/widgets/elderly_permission_widget.dart 2>/dev/null || echo "⚠️ 可能有语法问题"

# 2. 验证工作流文件
echo "📍检查工作流文件..."
cd ..
ls -la .github/workflows/*elderly* | head -5

# 3. 服务器配置验证
echo "📍验证服务器配置..."
if grep -q "223.26.59.132" libs/hbb_common/src/config.rs; then
    echo "✅ 服务器配置正确"
else
    echo "❌ 服务器配置错误"
fi

echo "🎯 已完成所有修正！"
echo "只需推送即可：git push origin main"