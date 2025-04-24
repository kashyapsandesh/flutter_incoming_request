Pod::Spec.new do |s|
  s.name             = 'flutter_incoming_request'
  s.version          = '1.0.0'
  s.summary          = 'A Flutter plugin for displaying incoming requests with CallKit integration.'
  s.description      = <<-DESC
A Flutter plugin for displaying incoming requests (like CallKit) with deep linking and custom UI.
                       DESC
  s.homepage         = 'https://github.com/kashyapsandesh/flutter_incoming_request'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.platform = :ios, '12.0'
  s.swift_version = '5.0'
end
