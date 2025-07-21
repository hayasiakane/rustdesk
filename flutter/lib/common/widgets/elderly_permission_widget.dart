import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/// Elderly-friendly permission check widget for RustDesk mobile
/// Designed for elderly users with large buttons and simple UI
class ElderlyPermissionWidget extends StatefulWidget {
  const ElderlyPermissionWidget({Key? key}) : super(key: key);

  @override
  State<ElderlyPermissionWidget> createState() => _ElderlyPermissionWidgetState();
}

class _ElderlyPermissionWidgetState extends State<ElderlyPermissionWidget> {
  static const platform = MethodChannel('mChannel');
  bool _permissionsGranted = true;
  bool _loading = false;

  @override
  void initState() {
    super.initState();
    _checkPermissions();
  }

  Future<void> _checkPermissions() async {
    try {
      final bool granted = await platform.invokeMethod('check_elderly_permissions');
      if (mounted) {
        setState(() {
          _permissionsGranted = granted;
        });
      }
    } on PlatformException catch (e) {
      debugPrint('Permission check failed: ${e.message}');
    }
  }

  Future<void> _requestPermissions() async {
    if (mounted) {
      setState(() {
        _loading = true;
      });
    }

    try {
      final bool success = await platform.invokeMethod('request_elderly_permissions');
      if (mounted) {
        setState(() {
          _permissionsGranted = success;
          _loading = false;
        });
      }
      
      // 如果有用户界面反馈需求，可以在这里添加提示
      debugPrint('Permissions request result: $success');
    } on PlatformException catch (e) {
      if (mounted) {
        setState(() {
          _loading = false;
        });
      }
      debugPrint('Permission request failed: ${e.message}');
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_permissionsGranted) {
      return const SizedBox.shrink(); // 如果权限已授予，不显示任何内容
    }

    return Container(
      margin: const EdgeInsets.all(20),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.orange[50],
        border: Border.all(color: Colors.orange, width: 2),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(
            Icons.warning_amber_rounded,
            size: 40,
            color: Colors.orange,
          ),
          const SizedBox(height: 12),
          const Text(
            '重要提醒',
            style: TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.bold,
              color: Colors.orange,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            '为了提供更好的远程协助服务，
需要获取以下权限：',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 18,
              color: Colors.black87,
            ),
          ),
          const SizedBox(height: 12),
          _buildPermissionList(),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            height: 50,
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.green,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(25),
                ),
                disabledBackgroundColor: Colors.grey,
              ),
              onPressed: _loading ? null : _requestPermissions,
              child: _loading
                  ? const SizedBox(
                      width: 30,
                      height: 30,
                      child: CircularProgressIndicator(
                        color: Colors.white,
                        strokeWidth: 3,
                      ),
                    )
                  : const Text(
                      '一键开启所有权限',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPermissionList() {
    final permissions = [
      '存储访问权限（发送/接收文件）',
      '麦克风权限（语音通话）',
      '摄像头权限（扫描二维码）',
      '系统悬浮窗（远程控制演示）',
      '忽略电池优化（保持稳定连接）',
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: permissions.map((permission) => 
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 3),
          child: Row(
            children: [
              const Icon(Icons.circle, size: 8, color: Colors.orange),
              const SizedBox(width: 8),
              Text(
                permission,
                style: const TextStyle(fontSize: 16),
              ),
            ],
          ),
        )
      ).toList(),
    );
  }
}