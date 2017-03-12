//
//  ViewController.swift
//  HelloWorld
//
//  Created by smok95 on 05/03/2017.
//  Copyright © 2017 smok95. All rights reserved.
//

import Cocoa

class ViewController: NSViewController {

    @IBOutlet var m_textview: MyTextView!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        /*
        NSEvent.addGlobalMonitorForEvents(matching: .keyDown, handler: (aEvent)-> NSEvent! in
            self.keyDown(aEvent)
            return aEvent
        )
 */
    }
    
    override var acceptsFirstResponder: Bool{
        return true
    }
    
    override func keyDown(with event: NSEvent) {
        Swift.print(event.keyCode)
    }

    override var representedObject: Any? {
        didSet {
        // Update the view, if already loaded.
        }
    }


}

