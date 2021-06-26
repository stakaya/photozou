#!c:/Perl/bin/perl
#!/usr/bin/perl

use strict;
use warnings;
use LWP::UserAgent;
use HTTP::Request;
use URI;
use XML::Simple;
use Encode;
use utf8;


print("Content-type: text/html\n\n");
binmode(STDIN);

my $id;
my $pass;
read(STDIN, $id,  256);
read(STDIN, $pass, 20);
$id = trim($id);
$pass = trim($pass);

my $apiurl = 'http://api.photozou.jp/rest/photo_album';
my $uri = URI->new($apiurl);
my $host = $uri->host;
my $port = $uri->port;
my $netloc = sprintf "%s:%d", $host, $port;
my $realm = 'photo';
my $ua = LWP::UserAgent->new;

# basic認証
$ua->credentials($netloc, $realm, $id, $pass);
my $res = $ua->request(HTTP::Request->new(GET => $apiurl));
if ($res->is_success) {
    my $result = XML::Simple::XMLin($res->content);
    if ($result->{stat} eq 'fail') {
        print "NG\n";
        print $result->{err};
    } else {
        print "OK\n";
        my $line;
        foreach $line (split(/\n/, $res->content())){
            if ($line =~ /^.*\<album_id\>(.*)$/) {
                #タグを除去
                $line =~ s/\<album_id\>//g;
                $line =~ s/\<\/album_id\>//g;
                print trim($line) . "\n";
            } elsif ($line =~ /^.*\<name\>(.*)$/) {
                #タグを除去
                $line =~ s/\<name\>\<\!\[CDATA\[//g;
                $line =~ s/\]\]\>\<\/name\>//g;
                print trim(Encode::encode('shiftjis', Encode::decode('utf8', $line))) . "\n";
            }
        }
    }
} else {
    print "NG\n";
    print $res->status_line;
    print "$id\n";
    print "$pass\n";
}

sub trim {
    my $val = shift;
    $val =~ s/^ *(.*?) *$/$1/;
    return $val;
}
