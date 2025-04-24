import Flutter
import UIKit
import CallKit
import PushKit

public class SwiftFlutterIncomingRequestPlugin: NSObject, FlutterPlugin, CXProviderDelegate {
    private var provider: CXProvider?
    private var callController: CXCallController?
    private var currentCallUUID: UUID?
    private var result: FlutterResult?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(
      name: "flutter_incoming_request",
      binaryMessenger: registrar.messenger()
    )
    let instance = SwiftFlutterIncomingRequestPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        self.result = result
    switch call.method {
    case "show":
      if let args = call.arguments as? [String: Any] {
        showRequest(args)
      }
    case "hide":
      if let args = call.arguments as? [String: Any], let id = args["id"] as? String {
        hideRequest(id)
      }
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  private func showRequest(_ data: [String: Any]) {
        let configuration = CXProviderConfiguration()
        configuration.supportsVideo = false
        configuration.maximumCallGroups = 1
        configuration.maximumCallsPerCallGroup = 1
        configuration.supportedHandleTypes = [.generic]
        
        provider = CXProvider(configuration: configuration)
        provider?.setDelegate(self, queue: nil)
        
        callController = CXCallController()
        
    let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .generic, value: data["name"] as? String ?? "Unknown")
        update.hasVideo = false
        
        currentCallUUID = UUID()
        provider?.reportNewIncomingCall(with: currentCallUUID!, update: update) { error in
            if let error = error {
                print("Failed to report incoming call: \(error.localizedDescription)")
                self.result?(false)
            } else {
                self.result?(true)
            }
        }
    }

    private func hideRequest(_ id: String) {
        if let uuid = currentCallUUID {
            provider?.reportCall(with: uuid, endedAt: Date(), reason: .remoteEnded)
            currentCallUUID = nil
        }
        result?(true)
    }

    // MARK: - CXProviderDelegate
    public func providerDidReset(_ provider: CXProvider) {
        // Clean up any existing calls
    }

    public func provider(_ provider: CXProvider, perform action: CXAnswerCallAction) {
        action.fulfill()
    }

    public func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        action.fulfill()
        currentCallUUID = nil
  }
}