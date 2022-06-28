//
//  Appearance.swift
//  react-native-notification-banner
//
//  Created by Sergei Golishnikov on 06/11/2021.
//

import Foundation


protocol Style {
    var icon: UIImage? { get }
    var backgroundColor: UIColor { get }
    var titleColor: UIColor { get }
    var messageColor: UIColor { get }
    
    var titleFont: Font { get }
}

struct Shadow {
    let offset: CGSize
    let color: UIColor
    let radius: CGFloat
    let opacity: Float
}

struct Font: Codable {
    var size: Double
    var family: String?
    var textAlign: String
}

struct ErrorStyle: Style {
    var icon: UIImage?
    var backgroundColor: UIColor
    var titleColor: UIColor
    var messageColor: UIColor
    var titleFont: Font
}

struct SuccessStyle: Style {
    var icon: UIImage?
    var backgroundColor: UIColor
    var titleColor: UIColor
    var messageColor: UIColor
    var titleFont: Font
}

struct InfoStyle: Style {
    var icon: UIImage?
    var backgroundColor: UIColor
    var titleColor: UIColor
    var messageColor: UIColor
    var titleFont: Font
}

struct DefaultStyle {
    var cornerRadius: CGFloat = 0
    var margin: CGFloat = 32
    var padding: CGFloat = 16
    var maxWidth: CGFloat = UIScreen.main.bounds.width - 32
    var minWidth: CGFloat = 150
    
    var shadow: Shadow
    
    var success: SuccessStyle
    var error: ErrorStyle
    var info: InfoStyle
}

var currentAppearance = DefaultStyle(
    cornerRadius: 1000,
    margin: 32,
    padding: 16,
    shadow: Shadow(offset: .init(width: 3, height: 1), color: .black, radius: 10, opacity: 0.5),
    success: SuccessStyle(
        icon: nil,
        backgroundColor: UIColor(red: 0.22, green: 0.80, blue: 0.46, alpha: 1.00),
        titleColor: .white,
        messageColor: .white,
        titleFont: Font(size: 17, family: nil, textAlign: "center")
    ),
    error: ErrorStyle(
        icon: nil,
        backgroundColor: UIColor(red: 0.90, green: 0.31, blue: 0.26, alpha: 1.00),
        titleColor: .white,
        messageColor: .white,
        titleFont: Font(size: 17, family: nil, textAlign: "center")
    ),
    info: InfoStyle(
        icon: nil,
        backgroundColor: UIColor(red: 0.23, green: 0.60, blue: 0.85, alpha: 1.00),
        titleColor: .white,
        messageColor: .white,
        titleFont: Font(size: 17, family: nil, textAlign: "center")
    )
)

func titleFont(type: BannerStyle) -> UIFont {
    let style = bannerAppearance(type: type)
    var font = UIFont.systemFont(ofSize: style.titleFont.size, weight: .bold)
    if let family = style.titleFont.family {
        font = UIFont(name: family, size: font.pointSize)!
    }
    return font
}

func messageFont(type: BannerStyle) -> UIFont {
    let style = bannerAppearance(type: type)
    return .systemFont(ofSize: 15)
}

func bannerAppearance(type: BannerStyle) -> Style {
    switch type {
    case .danger:
        return currentAppearance.error
    case .info:
        return currentAppearance.info
    case .success:
        return currentAppearance.success
    case .warning:
        return currentAppearance.error
    case .customView:
        fatalError("unsupported")
    }
}

func setNewAppearance(_ appearance: DefaultStyle) {
    currentAppearance = appearance
}


