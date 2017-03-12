//
//  MyTextView.swift
//  HelloWorld
//
//  Created by smok95 on 05/03/2017.
//  Copyright © 2017 smok95. All rights reserved.
//

import Foundation
import Cocoa

class MyTextView:NSTextView
{
    override var acceptsFirstResponder: Bool{
        return true
    }
    
    override func keyDown(with event: NSEvent) {
        Swift.print("keydown = \(event.keyCode), \(String(format:"%2x", event.keyCode))")
        super.keyDown(with: event)
    }
    
    override func performKeyEquivalent(with event: NSEvent) -> Bool {
        //Swift.print(event.keyCode)
        Swift.print("performKeyEquivalent  = \(event.keyCode), \(String(format:"%2x", event.keyCode))")
        //Swift.print("performKeyEquivalent call")
        return super.performKeyEquivalent(with: event)
    }
    
    override func flagsChanged(with event: NSEvent) {
        Swift.print("flagsChanged  = \(event.keyCode), \(String(format:"%2x", event.keyCode))")
        //Swift.print("flagsChanged = %4d, 0x%2X", event.keyCode, event.keyCode)
        super.flagsChanged(with: event)  }
}


