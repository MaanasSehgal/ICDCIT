"use client";
import Image from 'next/image';
import React, { useState, useEffect } from 'react';
import { Menu, X, ChevronDown } from 'lucide-react';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <>
      <nav className={`fixed top-0 w-full z-[100] transition-all duration-300 ${isScrolled ? 'bg-white/95 backdrop-blur-sm shadow-lg' : 'bg-white'
        }`}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-20">
            <a href='/' className="flex-shrink-0 flex items-center gap-3">
              <Image
                src="/icons/logo.png"
                alt="ICDCIT logo"
                width={48}
                height={48}
                className="object-contain"
              />
              <h1 className="text-2xl font-bold text-blue-600">
                ICDCIT
              </h1>
            </a>

            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="md:hidden inline-flex items-center justify-center p-2 rounded-md text-gray-700 hover:text-blue-600 hover:bg-blue-50 transition-colors"
              aria-expanded={isMenuOpen}
            >
              {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>

            <div className="hidden md:flex items-center gap-8">
              <div className="relative group">
                <button className="flex items-center gap-1 py-2 group hover:text-blue-600 transition-colors">
                  <span>Services</span>
                  <ChevronDown size={16} />
                </button>
                <div className="absolute top-full -left-4 w-48 bg-white shadow-lg rounded-lg py-2 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300">
                  <a href="/consultations" className="block px-4 py-2 hover:bg-blue-50 hover:text-blue-600 transition-colors">Doctor Consultations</a>
                  <a href="/ai-assistant" className="block px-4 py-2 hover:bg-blue-50 hover:text-blue-600 transition-colors">AI Health Assistant</a>
                  <a href="/group-chat" className="block px-4 py-2 hover:bg-blue-50 hover:text-blue-600 transition-colors">Group Chat</a>
                </div>
              </div>
              <a href="/about" className="hover:text-blue-600 transition-colors">About</a>
              <a href="/career" className="hover:text-blue-600 transition-colors">Career</a>
              <a href="/contact" className="hover:text-blue-600 transition-colors">Contact</a>
            </div>

            <div className="hidden md:flex items-center gap-4">
              <a href="/login" className="text-blue-600 font-medium hover:text-blue-800 transition-colors">
                Login
              </a>
              <a href="/signup" className="bg-blue-600 text-white px-6 py-2.5 rounded-full font-medium hover:bg-blue-700 transition-colors">
                Sign up
              </a>
            </div>
          </div>
        </div>
      </nav>

      {isMenuOpen && (
        <div className="fixed inset-0 bg-black/30 z-[90]" onClick={() => setIsMenuOpen(false)} />
      )}

      {/* Mobile menu */}
      <div
        className={`fixed inset-0 bg-white/95 backdrop-blur-sm transform z-[95] ${isMenuOpen ? 'translate-x-0' : 'translate-x-full'
          } transition-transform duration-300 ease-in-out`}
      >
        <div className="flex flex-col h-full px-4 pt-24 pb-6">
          <div className="flex flex-col gap-4">
            <a href="/" className="text-lg hover:text-blue-600 transition-colors py-2">Home</a>
            <a href="/services" className="text-lg hover:text-blue-600 transition-colors py-2">Services</a>
            <a href="/about" className="text-lg hover:text-blue-600 transition-colors py-2">About</a>
            <a href="/career" className="text-lg hover:text-blue-600 transition-colors py-2">Career</a>
            <a href="/contact" className="text-lg hover:text-blue-600 transition-colors py-2">Contact</a>
          </div>
          <div className="mt-auto space-y-4">
            <a href="/login" className="block w-full text-center text-blue-600 font-medium py-2 hover:text-blue-800 transition-colors">
              Login
            </a>
            <a href="/signup" className="block w-full text-center bg-blue-600 text-white py-3 rounded-full font-medium hover:bg-blue-700 transition-colors">
              Sign up
            </a>
          </div>
        </div>
      </div>
    </>
  );
};

export default Navbar;